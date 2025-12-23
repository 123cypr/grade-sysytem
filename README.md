# grade-system

## 简介
基于 Flask + SQLAlchemy 的中学学生成绩管理系统示例，支持学生/课程/班级的增删改查、成绩录入与修改、总分与平均分统计、班级排名查询，以及基于登录令牌的权限控制。默认使用 SQLite，本地或服务器可通过 `DATABASE_URL` 切换到 MySQL（推荐搭配 MySQL Workbench 管理数据）。

## 快速开始
1. 安装依赖
   ```bash
   pip install -r requirements.txt
   ```
2. 配置数据库（可选）  
   - 默认：SQLite 本地文件 `data.db`  
   - MySQL 示例：  
     ```bash
     export DATABASE_URL="mysql+pymysql://user:password@localhost:3306/grades"
     ```
3. 启动服务
   ```bash
   python app.py
   ```
   初始教师账号：`teacher / <随机密码或环境变量 DEFAULT_TEACHER_PASSWORD>`（未设置环境变量时会在日志输出随机密码）

## 主要接口
- `POST /login` 登录，返回 Bearer token。  
- 基础信息管理（教师权限）：  
  - `POST/GET/PUT/DELETE /classes` 班级管理  
  - `POST/GET/PUT/DELETE /courses` 课程管理  
  - `POST/GET/PUT/DELETE /students` 学生管理（创建学生时附带登录账号）  
- 成绩管理（教师权限）：  
  - `POST /grades` 成绩录入或修改（student_id, course_id, score）  
- 查询统计：  
- `GET /students/<id>/grades` 学生/教师查看个人成绩、总分、平均分  
- `GET /classes/<id>/ranking` 教师查看班级排名  

调用接口时在请求头附带 `Authorization: Bearer <token>`。更多字段示例可参考接口的返回 JSON。无需额外建表，服务启动时会自动建表并创建默认教师账号。令牌存储在内存且无过期设定，服务重启后需重新登录，生产部署请改为带过期时间的共享存储（如 Redis 或数据库）。
