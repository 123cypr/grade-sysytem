# 初中学生成绩管理系统执行规范（Spring Boot + Vue3 + MySQL）

本仓库用于落地初中学生成绩管理系统的交付规范，覆盖技术栈、数据库、后端、前端、部署与验证要求，可直接作为团队实施依据。

## 1. 技术栈与通用规范
- 后端：Java 17、Spring Boot 3.2.x、MyBatis-Plus 3.5.x、Maven 3.8+
- 前端：Node.js 18.x、Vue 3、Element Plus、Webpack 5、npm/yarn
- 数据库：MySQL 8.0（utf8mb4 / InnoDB）
- 仅支持电脑端（≥1366×768），兼容 Chrome 90+ / Edge 90+ / Firefox 88+
- 接口风格：RESTful，统一返回：
  ```json
  { "code": 200, "msg": "成功", "data": { ... }, "timestamp": 1735689600000 }
  ```
  错误码分段：100xx 用户模块、200xx 成绩模块、300xx 配置模块、400xx 通用。
- 编码规范：后端遵循阿里巴巴 Java 开发手册，公共接口入参与返回值需 Javadoc；前端遵循 ESLint，Vue 组件按 template / script / style 顺序，核心逻辑注释。

## 2. 架构与目录规划
- 后端（示例分层）：`controller -> service -> domain/entity -> mapper -> infrastructure`，统一异常与结果封装。
- 前端：`src/api`(Axios 封装) / `src/store`(Pinia) / `src/router` / `src/views`(按角色与业务分模块) / `src/components`。
- 日志：生产环境 INFO，敏感操作需审计日志（成绩修改、用户删除等）。

## 3. 数据库设计
### 3.1 库与通用字段
```sql
CREATE DATABASE IF NOT EXISTS junior_score_management
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```
所有表字段：`id BIGINT AUTO_INCREMENT PRIMARY KEY, create_time DATETIME, update_time DATETIME, deleted TINYINT DEFAULT 0`.

### 3.2 核心表（关键字段）
- `sys_user`：username, password(BCrypt), role_type(1=管理员/2=教师/3=学生/4=家长), status
- `student_info`：student_no(唯一), name, class_id, grade(1/2/3), parent_id
- `teacher_info`：teacher_no, name, subject_id；班级多对多关系通过 `teacher_class(teacher_id, class_id)` 关联
- `teacher_class`：teacher_id, class_id（教师与班级关联表）
- `subject_info`：name, full_score, is_total
- `exam_type`：name, exam_time, is_rank, is_public
- `score_info`：student_id, exam_id, subject_id, score, sports_items(JSON)，联合索引 `(student_id, exam_id, subject_id)`
- `score_remark`：student_id, exam_id, teacher_id, remark
- `score_archive`：用于历史归档

### 3.3 额外要求
- 体育中考分项示例：`{"跑步":30,"跳绳":15,"跳远":15}`，录入时自动汇总总分。`sports_items` JSON 期望 schema：key 为字符串分项名，value 为数字分值；需校验分项存在于配置表、分值不超该分项满分，总分不得超 `subject_info.full_score`。建议 JSON Schema 约束：
  ```json
  {
    "type": "object",
    "patternProperties": {
      "^.+$": { "type": "number", "minimum": 0 }
    },
    "additionalProperties": false
  }
  ```
- 逻辑删除使用 `deleted`；毕业年级数据归档到 `score_archive`。

## 4. 后端设计要点
- 认证与权限：基于角色的访问控制；家长绑定学生（学号+姓名）后仅能查看绑定学生。
- 参数校验：成绩范围 0-满分，学号格式，必填字段校验；异常返回明确提示。
- 模块与示例接口（仅列关键）：
  - 用户：登录（账号/验证码）、密码找回（手机号验证）、用户 CRUD、角色分配。
  - 基础配置：年级/班级/学科/考试类型 CRUD，体育分项配置，实验操作考试评分模式（分数/等级/合格）。
  - 成绩：单个录入、Excel 批量导入（模板下载）、修改申请与审批（管理员/班主任），修改日志记录原/新成绩、操作人、时间。
  - 查询：管理员全局与进度监控；教师班级/学科报表；学生/家长个人成绩、排名、评语；成绩咨询与回复。
  - 统计：班级/年级平均分、及格率、优秀率、分数段、历次考试对比。
- 定时与备份：每日 02:00 数据库自动备份；提供手动备份/恢复接口。
- 返回格式：统一包装，含 `code/msg/data/timestamp`；必要时包含分页信息。

## 5. 前端设计要点
- Axios 拦截器：请求自动附加 token/必要头；响应统一错误处理与登录失效跳转。
- 状态管理：Pinia 存储用户信息、角色、菜单与配置；路由守卫按角色控制访问。
- 角色页面：
  - 通用：登录（账号/密码/验证码/记住密码/错误提示）、忘记密码入口、个人中心（修改密码、查看信息）。
  - 管理员：系统配置（年级/班级/学科/考试类型，Excel 导入学生/教师）、用户管理（CRUD/分配权限/重置密码）、数据监控（录入进度、全校成绩汇总柱状/折线、操作日志）。
  - 教师：成绩录入（单个/Excel、体育分项录入、批量提交/撤回）、成绩分析（统计与对比曲线）、评语管理（单个/批量、模板）。
  - 学生/家长：成绩查询（按考试类型，体育分项单独展示，历史趋势）、成绩排名展示模式（具体名次/区间/隐藏，可按学校配置）、咨询反馈（提交问题、查看教师回复）。
- 交互：批量操作需加载动画与结果提示；表单提交前做前端校验（成绩不可超过满分）。

## 6. 部署与环境
- 后端：`mvn clean package -DskipTests`，产物 `junior-score-1.0.0.jar`；运行 `java -jar junior-score-1.0.0.jar --spring.profiles.active=prod`。
- `application-prod.yml` 需配置：数据库连接、端口(默认8080)、日志级别(INFO)。
- 前端：`npm install && npm run build`，静态资源部署至 Nginx。
- Nginx（示例）：启用 gzip，缓存 js/css/img，`/api` 反向代理至后端 8080。
- 测试账号：管理员 `admin/123456`；教师 `teacher01/123456`；学生 `student01/123456`；家长 `parent01/123456`。初始化数据：3 年级、各 2 班、10 学生、5 教师、全学科、2 次考试（月考+体育中考）。

## 7. 核心功能验证
- 管理员：配置体育分项（跑步30+跳绳15+跳远15），导入 5 名学生。
- 教师：录入体育分项成绩，系统自动汇总总分并生成班级报表。
- 学生/家长：登录查看体育中考成绩与排名，提交咨询并查看回复。
- 性能：批量导入 1000 条成绩响应 ≤3s；并发 100 用户查询成绩响应 ≤1s。测试基线环境建议：4 vCPU、8GB RAM、SSD（≥200MB/s），局域网/等效云同区部署。性能验证方法：使用 JMeter/Locust 预热后执行，导入场景以 1000 行 Excel/CSV 单批请求压测，查询场景以 100 并发持续 1 分钟记录 TP90/TP99。
