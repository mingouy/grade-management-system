# MyGrade 高校成绩管理系统

> Spring Boot 4 + Vue 3 + MySQL 8.0 + Docker 一键部署

## 技术栈

| 后端 | 版本 | 前端 | 版本 |
|------|------|------|------|
| Spring Boot | **4.0.2** | Vue | 3.4 |
| MyBatis-Plus | **3.5.16** (boot4-starter) | Element Plus | 2.6 |
| Java | **21** | Vite | 5.x |
| MySQL | 8.0 | Nginx | Alpine |
| Hutool | 5.8.34 | | |
| Docker Compose | 24.x | | |

## 功能模块

### 管理员
- 用户管理（增删改查）
- 教师信息管理
- 学生信息管理
- 专业管理
- 班级管理
- 课程管理
- 数据统计仪表盘

### 教师
- 查看我的班级和学生
- **发布课程**（新增/编辑/删除）
- 成绩录入
- 成绩查询（支持按课程、学期筛选）

### 学生
- 个人信息查看
- **在线选课**（选课/退课）
- 我的课程
- 我的成绩（含GPA计算）

## 快速启动

```bash
# 一键启动
docker-compose up --build -d

# 访问
# 前端: http://localhost:8088
# 后端: http://localhost:8081/api
# 数据库: localhost:3307
```

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher01 | 123456 |
| 学生 | 2022010101 | 123456 |

## 项目结构

```
├── backend/                    # Spring Boot 4 后端
│   ├── src/main/java/com/grade/
│   │   ├── controller/         # 12个控制器
│   │   ├── entity/             # 9个实体类
│   │   ├── service/            # 7个服务接口+实现
│   │   ├── mapper/             # 8个MyBatis-Plus Mapper
│   │   ├── config/             # CORS、MyBatis-Plus、WebMvc配置
│   │   ├── interceptor/        # RBAC权限拦截器
│   │   └── exception/          # 全局异常处理
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── api/                # 10个API模块
│   │   ├── views/
│   │   │   ├── admin/          # 6个管理员页面
│   │   │   ├── teacher/        # 4个教师页面
│   │   │   └── student/        # 4个学生页面
│   │   ├── layout/             # 侧边栏布局
│   │   ├── router/             # 路由+权限守卫
│   │   └── store/              # Pinia状态管理
│   ├── package.json
│   └── Dockerfile
├── sql/                        # 建表脚本+测试数据
│   ├── schema.sql
│   └── test-data.sql
├── docker-compose.yml
└── docs/
    └── 设计报告.md
```

## 数据规模

| 数据 | 数量 |
|------|------|
| 学生 | 60人 |
| 教师 | 8人 |
| 专业 | 3个 |
| 班级 | 12个 |
| 课程 | 16门 |
| 选课记录 | 167条 |
| 成绩记录 | 110条 |

## 设计报告

详见 [docs/设计报告.md](docs/设计报告.md)
