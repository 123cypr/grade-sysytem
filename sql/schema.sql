-- 学生成绩管理系统数据库脚本（MySQL 8.0+）
-- 包含库、表、约束、视图、触发器、存储过程/函数以及示例数据

CREATE DATABASE IF NOT EXISTS grade_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE grade_system;

-- 清理旧对象，便于反复执行脚本
DROP VIEW IF EXISTS v_student_transcript;
DROP VIEW IF EXISTS v_course_overview;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS students;
DROP PROCEDURE IF EXISTS sp_upsert_score;
DROP FUNCTION IF EXISTS fn_calculate_gpa;

-- 学生基本信息
CREATE TABLE students (
  student_id   INT AUTO_INCREMENT PRIMARY KEY,
  student_no   VARCHAR(20) NOT NULL UNIQUE,
  full_name    VARCHAR(50) NOT NULL,
  gender       ENUM('M','F') NOT NULL,
  birth_date   DATE,
  class_name   VARCHAR(50),
  admission_year INT NOT NULL CHECK (admission_year >= 1990),
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 教师信息
CREATE TABLE teachers (
  teacher_id  INT AUTO_INCREMENT PRIMARY KEY,
  teacher_no  VARCHAR(20) NOT NULL UNIQUE,
  full_name   VARCHAR(50) NOT NULL,
  title       VARCHAR(30),
  phone       VARCHAR(30),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程信息
CREATE TABLE courses (
  course_id  INT AUTO_INCREMENT PRIMARY KEY,
  course_no  VARCHAR(20) NOT NULL UNIQUE,
  course_name VARCHAR(100) NOT NULL,
  credit     DECIMAL(3,1) NOT NULL CHECK (credit > 0),
  teacher_id INT NOT NULL,
  term       VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
);

-- 选课及成绩（允许先选课后录入成绩）
CREATE TABLE enrollments (
  enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  course_id INT NOT NULL,
  term VARCHAR(20) NOT NULL,
  score DECIMAL(5,2),
  grade_point DECIMAL(3,2),
  UNIQUE KEY uq_student_course_term (student_id, course_id, term),
  CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES students(student_id),
  CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
  CHECK (score IS NULL OR (score BETWEEN 0 AND 100))
);

CREATE INDEX idx_enroll_student ON enrollments(student_id);
CREATE INDEX idx_enroll_course ON enrollments(course_id);

-- 视图：学生成绩单
CREATE VIEW v_student_transcript AS
SELECT
  s.student_no,
  s.full_name AS student_name,
  s.class_name,
  c.course_no,
  c.course_name,
  c.credit,
  e.term,
  e.score,
  e.grade_point
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN courses c ON e.course_id = c.course_id;

-- 视图：课程整体情况
CREATE VIEW v_course_overview AS
SELECT
  c.course_no,
  c.course_name,
  c.term,
  t.full_name AS teacher_name,
  COUNT(e.enrollment_id) AS selected_count,
  ROUND(AVG(e.score), 2) AS avg_score
FROM courses c
LEFT JOIN teachers t ON c.teacher_id = t.teacher_id
LEFT JOIN enrollments e ON e.course_id = c.course_id
GROUP BY c.course_id;

DELIMITER $$

-- 成绩转绩点的函数
CREATE FUNCTION fn_calculate_gpa(p_student_id INT)
RETURNS DECIMAL(4,2)
DETERMINISTIC
BEGIN
  DECLARE v_gpa DECIMAL(4,2);
  SELECT COALESCE(SUM(e.grade_point * c.credit) / NULLIF(SUM(c.credit), 0), 0)
  INTO v_gpa
  FROM enrollments e
  JOIN courses c ON e.course_id = c.course_id
  WHERE e.student_id = p_student_id
    AND e.score IS NOT NULL;
  RETURN v_gpa;
END$$

-- 录入或更新成绩的存储过程（按学号/课号操作，便于前端调用）
CREATE PROCEDURE sp_upsert_score(
  IN p_student_no VARCHAR(20),
  IN p_course_no  VARCHAR(20),
  IN p_term       VARCHAR(20),
  IN p_score      DECIMAL(5,2)
)
BEGIN
  DECLARE v_student_id INT;
  DECLARE v_course_id INT;

  SELECT student_id INTO v_student_id FROM students WHERE student_no = p_student_no;
  IF v_student_id IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '学生不存在';
  END IF;

  SELECT course_id INTO v_course_id FROM courses WHERE course_no = p_course_no;
  IF v_course_id IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '课程不存在';
  END IF;

  INSERT INTO enrollments (student_id, course_id, term, score)
  VALUES (v_student_id, v_course_id, p_term, p_score)
  ON DUPLICATE KEY UPDATE score = VALUES(score);
END$$

-- 触发器：写入成绩时自动换算绩点
CREATE TRIGGER trg_enrollments_before_ins
BEFORE INSERT ON enrollments
FOR EACH ROW
BEGIN
  IF NEW.score IS NOT NULL THEN
    SET NEW.grade_point = CASE
      WHEN NEW.score >= 90 THEN 4.0
      WHEN NEW.score >= 85 THEN 3.7
      WHEN NEW.score >= 80 THEN 3.3
      WHEN NEW.score >= 75 THEN 3.0
      WHEN NEW.score >= 70 THEN 2.7
      WHEN NEW.score >= 66 THEN 2.3
      WHEN NEW.score >= 62 THEN 2.0
      WHEN NEW.score >= 60 THEN 1.7
      ELSE 0
    END;
  END IF;
END$$

CREATE TRIGGER trg_enrollments_before_upd
BEFORE UPDATE ON enrollments
FOR EACH ROW
BEGIN
  IF NEW.score IS NOT NULL AND (NEW.score <> OLD.score OR OLD.grade_point IS NULL) THEN
    SET NEW.grade_point = CASE
      WHEN NEW.score >= 90 THEN 4.0
      WHEN NEW.score >= 85 THEN 3.7
      WHEN NEW.score >= 80 THEN 3.3
      WHEN NEW.score >= 75 THEN 3.0
      WHEN NEW.score >= 70 THEN 2.7
      WHEN NEW.score >= 66 THEN 2.3
      WHEN NEW.score >= 62 THEN 2.0
      WHEN NEW.score >= 60 THEN 1.7
      ELSE 0
    END;
  END IF;
END$$

DELIMITER ;

-- 示例数据
INSERT INTO students (student_no, full_name, gender, birth_date, class_name, admission_year)
VALUES
('2023001', '张三', 'M', '2006-03-01', '高一(1)班', 2023),
('2023002', '李四', 'F', '2006-07-12', '高一(2)班', 2023),
('2023003', '王五', 'M', '2005-11-21', '高二(1)班', 2022);

INSERT INTO teachers (teacher_no, full_name, title, phone)
VALUES
('T001', '刘老师', '高级教师', '13800000001'),
('T002', '周老师', '一级教师', '13800000002');

INSERT INTO courses (course_no, course_name, credit, teacher_id, term)
VALUES
('C001', '数学', 4.0, 1, '2023-秋'),
('C002', '语文', 3.5, 2, '2023-秋'),
('C003', '英语', 3.0, 1, '2023-秋');

INSERT INTO enrollments (student_id, course_id, term, score) VALUES
(1, 1, '2023-秋', 95),
(1, 2, '2023-秋', 88),
(2, 1, '2023-秋', 78),
(2, 3, '2023-秋', 82),
(3, 1, '2023-秋', NULL); -- 尚未录入成绩

-- 常用查询示例
-- 1) 查看学生成绩单
-- SELECT * FROM v_student_transcript WHERE student_no = '2023001';
-- 2) 计算某学生 GPA
-- SELECT fn_calculate_gpa(student_id) FROM students WHERE student_no = '2023001';
-- 3) 调用存储过程录入/更新成绩
-- CALL sp_upsert_score('2023003', 'C001', '2023-秋', 91);
-- 4) 查看课程整体情况
-- SELECT * FROM v_course_overview;
