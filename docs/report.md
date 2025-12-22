# 中学学生成绩管理系统课程设计报告

> 适合初学者的完整实验步骤，配套 SQL 脚本见 `sql/schema.sql`。

## 1. 需求分析

### 1.1 系统功能概述
- 学生信息管理：录入/修改/查询学生基本信息。
- 教师信息管理：维护任课教师与职称。
- 课程管理：课程开设、学分、任课教师、学期信息。
- 选课与成绩管理：选课登记，成绩录入、修改，自动换算绩点。
- 成绩查询与统计：学生成绩单、课程平均分、GPA 计算。

### 1.2 总功能图述
```
用户 -> 登录/权限 -> 学生管理
                   -> 教师管理
                   -> 课程管理
                   -> 选课与成绩录入 -> 成绩查询/统计 -> 导出报表
```

### 1.3 系统流程图（文字化）
1. 管理员创建教师/课程 -> 教师发布课程 -> 学生选课。
2. 教师或教务录入成绩（调用存储过程或直接更新表）。
3. 触发器自动换算绩点 -> 视图生成成绩单。
4. 查询视图 / 调用函数计算 GPA，生成报表。

### 1.4 数据流图（文字化）
- 外部实体：学生、教师、管理员。
- 数据存储：`students`、`teachers`、`courses`、`enrollments`。
- 数据流：用户输入 -> 业务逻辑 -> SQL（存储过程/触发器）-> 数据表/视图 -> 查询结果返回用户。

### 1.5 数据字典（核心字段）
| 表 | 关键字段 | 说明 |
| --- | --- | --- |
| students | student_id(PK)、student_no(唯一)、full_name、gender、class_name、admission_year | 学生基本信息 |
| teachers | teacher_id(PK)、teacher_no(唯一)、full_name、title | 教师信息 |
| courses | course_id(PK)、course_no(唯一)、course_name、credit、teacher_id(FK)、term | 课程与学分 |
| enrollments | enrollment_id(PK)、student_id(FK)、course_id(FK)、term、score、grade_point | 选课与成绩（成绩可为空） |

## 2. 概念结构设计

### 2.1 实体
- 学生（Student）、教师（Teacher）、课程（Course）、选课/成绩（Enrollment）。

### 2.2 联系
- 学生-选课：一对多。
- 课程-选课：一对多。
- 教师-课程：一对多。

### 2.3 ER 图（文字描述）
- Student(学号, 姓名, 性别, 班级, 入学年) ——< Enrollment(成绩, 绩点, 学期) >—— Course(课号, 课程名, 学分, 学期) —— Teacher(工号, 姓名, 职称)。

## 3. 逻辑结构设计

### 3.1 E-R 向关系模式转换
- Student( student_id PK, student_no UK, … )
- Teacher( teacher_id PK, teacher_no UK, … )
- Course( course_id PK, course_no UK, teacher_id FK, … )
- Enrollment( enrollment_id PK, student_id FK, course_id FK, term, score, grade_point, UK(student_id, course_id, term) )

### 3.2 关系优化
- 主键/唯一约束保证标识唯一。
- CHECK 约束限制成绩范围、学分为正数。
- 对 student_id、course_id 建立索引，加速成绩查询。

### 3.3 约束说明
- 外键：确保学生、课程、教师存在。
- CHECK：成绩 0-100，学分 > 0，入学年 >= 1990。
- 触发器：自动将分数换算为绩点。

### 3.4 基本表
详见 `sql/schema.sql` 中的建表语句。

## 4. 物理结构设计
- 存储结构：InnoDB，支持事务与外键。
- 编码：utf8mb4，便于中文存储。
- 索引：`enrollments(student_id)`、`enrollments(course_id)` 便于按学生/课程查询。

## 5. 数据库的实现
1. 安装 MySQL 8.0+。
2. 执行脚本：`mysql -u root -p < sql/schema.sql`。
3. 脚本内容包括：数据库与表、视图、触发器、存储过程/函数、示例数据。
4. 通过示例查询快速验证：
   ```sql
   USE grade_system;
   SELECT * FROM v_student_transcript WHERE student_no = '2023001';
   SELECT fn_calculate_gpa(student_id) FROM students WHERE student_no = '2023001';
   CALL sp_upsert_score('2023003', 'C001', '2023-秋', 91);
   SELECT * FROM v_course_overview;
   ```

## 6. 应用系统实现（示例）

### 6.1 连接与基本操作（Python 示例）
```python
import mysql.connector

conn = mysql.connector.connect(
    host="localhost", user="root", password="your_pass", database="grade_system"
)
cur = conn.cursor(dictionary=True)

# 查询成绩单
cur.execute("SELECT * FROM v_student_transcript WHERE student_no=%s", ("2023001",))
print(cur.fetchall())

# 录入/更新成绩（调用存储过程）
cur.callproc("sp_upsert_score", ("2023003", "C001", "2023-秋", 91))
conn.commit()

cur.close()
conn.close()
```

### 6.2 前端/交互要点
- 表单校验：成绩 0-100；必填字段非空。
- 录入成绩统一调用存储过程，减少重复 SQL。
- 查询页面直接使用视图 `v_student_transcript`、统计页使用 `v_course_overview`。

## 7. 实验步骤（面向小白）
1. 安装 MySQL，确认命令行可用。
2. 克隆仓库，进入目录：`cd grade-sysytem`。
3. 执行 `mysql -u root -p < sql/schema.sql` 初始化数据库和示例数据。
4. 在 MySQL 中运行示例查询，确认视图、函数、存储过程可用。
5. 结合示例 Python 代码或任意语言的数据库驱动，实现登录后对学生、课程、成绩的增删改查。
6. 使用视图/函数输出成绩单或 GPA 结果，完成实验报告截图与说明。

## 8. 结果说明
- 数据完整性：通过主键/唯一/外键/CHECK/触发器保证。
- 视图：`v_student_transcript`（学生成绩单）、`v_course_overview`（课程统计）。
- 触发器：录入/更新成绩时自动计算绩点。
- 存储过程/函数：`sp_upsert_score`（录入成绩），`fn_calculate_gpa`（计算 GPA）。
- 示例数据：脚本内已提供，可直接验证查询与统计功能。
