import os
import secrets
from functools import wraps

from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import check_password_hash, generate_password_hash


app = Flask(__name__)
database_url = os.environ.get("DATABASE_URL", "sqlite:///data.db")
app.config["SQLALCHEMY_DATABASE_URI"] = database_url
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

db = SQLAlchemy(app)
tokens: dict[str, int] = {}


class Class(db.Model):
    __tablename__ = "classes"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), unique=True, nullable=False)
    students = db.relationship("Student", back_populates="classroom", cascade="all, delete-orphan")


class Student(db.Model):
    __tablename__ = "students"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), nullable=False)
    class_id = db.Column(db.Integer, db.ForeignKey("classes.id"), nullable=False)
    classroom = db.relationship("Class", back_populates="students")
    user = db.relationship("User", back_populates="student", uselist=False, cascade="all, delete-orphan")
    grades = db.relationship("Grade", back_populates="student", cascade="all, delete-orphan")


class Course(db.Model):
    __tablename__ = "courses"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), unique=True, nullable=False)
    grades = db.relationship("Grade", back_populates="course", cascade="all, delete-orphan")


class Grade(db.Model):
    __tablename__ = "grades"
    __table_args__ = (db.UniqueConstraint("student_id", "course_id", name="uix_student_course"),)

    id = db.Column(db.Integer, primary_key=True)
    student_id = db.Column(db.Integer, db.ForeignKey("students.id"), nullable=False)
    course_id = db.Column(db.Integer, db.ForeignKey("courses.id"), nullable=False)
    score = db.Column(db.Float, nullable=False)

    student = db.relationship("Student", back_populates="grades")
    course = db.relationship("Course", back_populates="grades")


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(64), unique=True, nullable=False)
    password_hash = db.Column(db.String(256), nullable=False)
    role = db.Column(db.String(16), nullable=False)  # teacher / student
    student_id = db.Column(db.Integer, db.ForeignKey("students.id"))

    student = db.relationship("Student", back_populates="user")

    def set_password(self, password: str) -> None:
        self.password_hash = generate_password_hash(password)

    def check_password(self, password: str) -> bool:
        return check_password_hash(self.password_hash, password)


def create_default_teacher() -> None:
    if not User.query.filter_by(role="teacher").first():
        teacher = User(username="teacher", role="teacher")
        teacher.set_password("teacher123")
        db.session.add(teacher)
        db.session.commit()


def require_auth(roles: list[str] | None = None):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            auth_header = request.headers.get("Authorization", "")
            token = auth_header.removeprefix("Bearer ").strip()
            user_id = tokens.get(token)
            if not user_id:
                return jsonify({"error": "unauthorized"}), 401
            user = User.query.get(user_id)
            if not user:
                return jsonify({"error": "unauthorized"}), 401
            if roles and user.role not in roles:
                return jsonify({"error": "forbidden"}), 403
            request.user = user
            return func(*args, **kwargs)

        return wrapper

    return decorator


def student_accessible(student_id: int, user: User) -> bool:
    return user.role == "teacher" or (user.role == "student" and user.student_id == student_id)


@app.post("/login")
def login():
    payload = request.get_json(force=True)
    username = payload.get("username")
    password = payload.get("password")
    if not username or not password:
        return jsonify({"error": "username and password required"}), 400
    user = User.query.filter_by(username=username).first()
    if not user or not user.check_password(password):
        return jsonify({"error": "invalid credentials"}), 401
    token = secrets.token_hex(16)
    tokens[token] = user.id
    return jsonify({"token": token, "role": user.role})


@app.post("/classes")
@require_auth(["teacher"])
def create_class():
    payload = request.get_json(force=True)
    name = payload.get("name")
    if not name:
        return jsonify({"error": "name required"}), 400
    if Class.query.filter_by(name=name).first():
        return jsonify({"error": "class already exists"}), 400
    classroom = Class(name=name)
    db.session.add(classroom)
    db.session.commit()
    return jsonify({"id": classroom.id, "name": classroom.name}), 201


@app.get("/classes")
@require_auth(["teacher"])
def list_classes():
    classes = Class.query.all()
    return jsonify([{"id": c.id, "name": c.name} for c in classes])


@app.put("/classes/<int:class_id>")
@require_auth(["teacher"])
def update_class(class_id: int):
    classroom = Class.query.get_or_404(class_id)
    payload = request.get_json(force=True)
    name = payload.get("name")
    if name:
        classroom.name = name
        db.session.commit()
    return jsonify({"id": classroom.id, "name": classroom.name})


@app.delete("/classes/<int:class_id>")
@require_auth(["teacher"])
def delete_class(class_id: int):
    classroom = Class.query.get_or_404(class_id)
    db.session.delete(classroom)
    db.session.commit()
    return jsonify({"status": "deleted"})


@app.post("/students")
@require_auth(["teacher"])
def create_student():
    payload = request.get_json(force=True)
    name = payload.get("name")
    class_id = payload.get("class_id")
    username = payload.get("username")
    password = payload.get("password")
    if not all([name, class_id, username, password]):
        return jsonify({"error": "name, class_id, username, password required"}), 400
    if User.query.filter_by(username=username).first():
        return jsonify({"error": "username already exists"}), 400
    Class.query.get_or_404(class_id)
    student = Student(name=name, class_id=class_id)
    db.session.add(student)
    db.session.flush()
    user = User(username=username, role="student", student_id=student.id)
    user.set_password(password)
    db.session.add(user)
    db.session.commit()
    return jsonify({"id": student.id, "name": student.name, "class_id": student.class_id}), 201


@app.get("/students")
@require_auth(["teacher"])
def list_students():
    students = Student.query.all()
    return jsonify(
        [
            {
                "id": s.id,
                "name": s.name,
                "class_id": s.class_id,
                "class_name": s.classroom.name,
            }
            for s in students
        ]
    )


@app.put("/students/<int:student_id>")
@require_auth(["teacher"])
def update_student(student_id: int):
    student = Student.query.get_or_404(student_id)
    payload = request.get_json(force=True)
    name = payload.get("name")
    class_id = payload.get("class_id")
    if name:
        student.name = name
    if class_id:
        Class.query.get_or_404(class_id)
        student.class_id = class_id
    db.session.commit()
    return jsonify({"id": student.id, "name": student.name, "class_id": student.class_id})


@app.delete("/students/<int:student_id>")
@require_auth(["teacher"])
def delete_student(student_id: int):
    student = Student.query.get_or_404(student_id)
    db.session.delete(student)
    db.session.commit()
    return jsonify({"status": "deleted"})


@app.post("/courses")
@require_auth(["teacher"])
def create_course():
    payload = request.get_json(force=True)
    name = payload.get("name")
    if not name:
        return jsonify({"error": "name required"}), 400
    if Course.query.filter_by(name=name).first():
        return jsonify({"error": "course already exists"}), 400
    course = Course(name=name)
    db.session.add(course)
    db.session.commit()
    return jsonify({"id": course.id, "name": course.name}), 201


@app.get("/courses")
@require_auth(["teacher"])
def list_courses():
    courses = Course.query.all()
    return jsonify([{"id": c.id, "name": c.name} for c in courses])


@app.put("/courses/<int:course_id>")
@require_auth(["teacher"])
def update_course(course_id: int):
    course = Course.query.get_or_404(course_id)
    payload = request.get_json(force=True)
    name = payload.get("name")
    if name:
        course.name = name
        db.session.commit()
    return jsonify({"id": course.id, "name": course.name})


@app.delete("/courses/<int:course_id>")
@require_auth(["teacher"])
def delete_course(course_id: int):
    course = Course.query.get_or_404(course_id)
    db.session.delete(course)
    db.session.commit()
    return jsonify({"status": "deleted"})


@app.post("/grades")
@require_auth(["teacher"])
def upsert_grade():
    payload = request.get_json(force=True)
    student_id = payload.get("student_id")
    course_id = payload.get("course_id")
    score = payload.get("score")
    if student_id is None or course_id is None or score is None:
        return jsonify({"error": "student_id, course_id, score required"}), 400
    Student.query.get_or_404(student_id)
    Course.query.get_or_404(course_id)
    grade = Grade.query.filter_by(student_id=student_id, course_id=course_id).first()
    if grade:
        grade.score = score
    else:
        grade = Grade(student_id=student_id, course_id=course_id, score=score)
        db.session.add(grade)
    db.session.commit()
    return jsonify({"id": grade.id, "student_id": student_id, "course_id": course_id, "score": grade.score})


def grade_summary_for_student(student_id: int):
    grades = Grade.query.filter_by(student_id=student_id).all()
    scores = [g.score for g in grades]
    total = sum(scores)
    average = total / len(scores) if scores else 0
    return grades, total, average


@app.get("/students/<int:student_id>/grades")
@require_auth(["teacher", "student"])
def student_grades(student_id: int):
    user = request.user
    if not student_accessible(student_id, user):
        return jsonify({"error": "forbidden"}), 403
    student = Student.query.get_or_404(student_id)
    grades, total, average = grade_summary_for_student(student_id)
    return jsonify(
        {
            "student": {"id": student.id, "name": student.name},
            "grades": [{"course": g.course.name, "score": g.score} for g in grades],
            "total": total,
            "average": average,
        }
    )


@app.get("/classes/<int:class_id>/ranking")
@require_auth(["teacher"])
def class_ranking(class_id: int):
    Class.query.get_or_404(class_id)
    students = Student.query.filter_by(class_id=class_id).all()
    ranking = []
    for student in students:
        _, total, average = grade_summary_for_student(student.id)
        ranking.append({"student_id": student.id, "name": student.name, "total": total, "average": average})
    ranking.sort(key=lambda item: item["total"], reverse=True)
    for index, item in enumerate(ranking, start=1):
        item["rank"] = index
    return jsonify(ranking)


@app.get("/health")
def health():
    return jsonify({"status": "ok"})


def bootstrap():
    db.create_all()
    create_default_teacher()


with app.app_context():
    bootstrap()


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))
