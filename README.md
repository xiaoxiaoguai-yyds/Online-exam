<div align="center">

# 🎓 在线考试系统

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue.js-3.5.18-4FC08D" alt="Vue.js">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/Java-17-orange" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
  <img src="https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey" alt="Platform">
</p>

<p align="center">
  <strong>一个功能完整、界面美观的在线考试管理系统</strong>
</p>

<p align="center">
  支持教师创建考试、学生在线答题、智能评分、成绩统计等功能
</p>

<p align="center">
  <a href="#-快速开始">快速开始</a> •
  <a href="#-功能特性">功能特性</a> •
  <a href="#-在线演示">在线演示</a> •
  <a href="#-技术栈">技术栈</a> •
  <a href="#-部署指南">部署指南</a>
</p>

</div>

---

## 📋 系统概述

本系统是一个完整的在线考试解决方案，采用前后端分离架构，为教育机构提供便捷的考试管理平台。

## 🎯 在线演示

> **注意**: 演示环境仅供体验，请勿上传重要数据

| 角色 | 用户名 | 密码 | 功能 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 完整的系统管理功能 |
| 教师 | teacher | teacher123 | 考试创建、题目管理、成绩查看 |
| 学生 | student | student123 | 参加考试、查看成绩 |

## 📸 系统截图

<details>
<summary>点击查看系统截图</summary>

### 🏠 管理员仪表板
![管理员仪表板](docs/images/admin-dashboard.png)

### 📝 考试管理
![考试管理](docs/images/exam-management.png)

### 📊 题目管理
![题目管理](docs/images/question-management.png)

### 👥 学生管理
![学生管理](docs/images/student-management.png)

### 🎓 学生考试界面
![学生考试界面](docs/images/student-exam.png)

### 📈 成绩统计
![成绩统计](docs/images/results-statistics.png)

</details>

## ✨ 功能特性

<table>
<tr>
<td width="50%">

### 🎓 教师端功能

- **📝 考试管理**
  - 创建、编辑、删除考试
  - 设置考试时间和时长
  - 考试状态管理（草稿/发布/结束）
  
- **📊 题目管理**
  - 支持单选题、多选题、判断题
  - 题目分类和标签管理
  - 批量导入/导出题目
  
- **👥 学生管理**
  - 学生信息管理
  - 考试权限分配
  - 学习进度跟踪
  
- **📈 成绩统计**
  - 实时成绩查看
  - 统计分析报表
  - 成绩导出功能

</td>
<td width="50%">

### 👨‍🎓 学生端功能

- **🎯 在线考试**
  - 流畅的答题体验
  - 自动保存答案
  - 倒计时提醒
  
- **📋 成绩查询**
  - 历史考试成绩
  - 详细答题分析
  - 错题回顾
  
- **👤 个人中心**
  - 个人信息管理
  - 密码修改
  - 考试历史记录
  
- **📱 响应式设计**
  - 支持PC、平板、手机
  - 自适应界面布局
  - 优秀的用户体验

</td>
</tr>
</table>

## 🛠️ 技术栈

<table>
<tr>
<td width="50%">

### 🔧 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| ![Java](https://img.shields.io/badge/Java-17-orange?logo=java) | 17+ | 编程语言 |
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?logo=spring) | 3.5.5 | 主框架 |
| ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql) | 8.0+ | 数据库 |
| ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.5.5-green?logo=spring) | 3.5.5 | ORM框架 |
| ![Maven](https://img.shields.io/badge/Maven-3.8+-red?logo=apache-maven) | 3.8+ | 构建工具 |

**核心特性:**
- 🔐 RESTful API设计
- 🛡️ JWT身份认证
- 📊 数据库事务管理
- 🔄 自动数据验证
- 📝 API文档自动生成

</td>
<td width="50%">

### 🎨 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| ![Vue.js](https://img.shields.io/badge/Vue.js-3.5.18-4FC08D?logo=vue.js) | 3.5.18 | 前端框架 |
| ![Vite](https://img.shields.io/badge/Vite-6.0.7-646CFF?logo=vite) | 6.0.7 | 构建工具 |
| ![Element Plus](https://img.shields.io/badge/Element%20Plus-2.9.1-409EFF?logo=element) | 2.9.1 | UI组件库 |
| ![Vue Router](https://img.shields.io/badge/Vue%20Router-4.5.0-4FC08D?logo=vue.js) | 4.5.0 | 路由管理 |
| ![Axios](https://img.shields.io/badge/Axios-1.7.9-5A29E4?logo=axios) | 1.7.9 | HTTP客户端 |
| ![Electron](https://img.shields.io/badge/Electron-33.2.1-47848F?logo=electron) | 33.2.1 | 桌面应用 |

**核心特性:**
- 📱 响应式设计
- ⚡ 快速热重载
- 🎯 组件化开发
- 🔄 状态管理
- 🖥️ 跨平台桌面应用

</td>
</tr>
</table>

## 🚀 快速开始

### 📋 环境要求

在开始之前，请确保您的系统已安装以下软件：

| 软件 | 版本要求 | 下载链接 |
|------|----------|----------|
| ☕ Java | 17+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| 🟢 Node.js | 16+ | [Node.js官网](https://nodejs.org/) |
| 🐬 MySQL | 8.0+ | [MySQL官网](https://dev.mysql.com/downloads/) |
| 📦 Maven | 3.8+ | [Maven官网](https://maven.apache.org/download.cgi) |

### 🗄️ 数据库配置

<details>
<summary>点击展开数据库配置步骤</summary>

#### 1. 创建数据库
```sql
-- 创建数据库
CREATE DATABASE question_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'exam_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON question_db.* TO 'exam_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. 导入数据库脚本
```bash
# 方式1：使用命令行
mysql -u root -p question_db < sql/question_db.sql

# 方式2：使用MySQL Workbench
# 打开MySQL Workbench -> 连接数据库 -> 导入sql/question_db.sql文件
```

#### 3. 验证数据库
```sql
USE question_db;
SHOW TABLES;
-- 应该看到：exams, exam_questions, questions, student_answers, users 等表
```

</details>

### 🔧 后端部署

<details>
<summary>点击展开后端部署步骤</summary>

#### 1. 克隆项目
```bash
# 克隆仓库
git clone https://github.com/xiaoxiaoguai-yyds/Online-exam.git
cd Online-exam

# 或者下载ZIP文件并解压
```

#### 2. 配置数据库连接
复制配置文件模板：
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

编辑配置文件：
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/question_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 服务器配置
server.port=8080
```

#### 3. 编译和运行
```bash
# 清理并编译
mvn clean compile

# 运行测试（可选）
mvn test

# 启动应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/question-0.0.1-SNAPSHOT.jar
```

✅ **后端服务将在 http://localhost:8080 启动**

</details>

### 🎨 前端部署

<details>
<summary>点击展开前端部署步骤</summary>

#### 1. 进入前端目录
```bash
cd vue
```

#### 2. 安装依赖
```bash
# 使用npm
npm install

# 或使用yarn（推荐）
yarn install

# 或使用pnpm（更快）
pnpm install
```

#### 3. 配置API地址
编辑 `src/config/api.js`：
```javascript
const API_BASE_URL = 'http://localhost:8080';
export default API_BASE_URL;
```

#### 4. 启动开发服务器
```bash
# 启动开发服务器
npm run dev

# 或者
yarn dev
```

#### 5. 构建生产版本（可选）
```bash
# 构建Web版本
npm run build

# 构建Electron桌面应用
npm run electron:build
```

✅ **前端应用将在 http://localhost:5173 启动**

</details>

### 🎯 验证安装

访问以下地址验证系统是否正常运行：

- 🌐 **前端应用**: http://localhost:5173
- 🔧 **后端API**: http://localhost:8080
- 📊 **API文档**: http://localhost:8080/swagger-ui.html（如果配置了Swagger）

使用默认账户登录：
- 👨‍💼 **管理员**: admin / admin123
- 👨‍🏫 **教师**: teacher / teacher123  
- 👨‍🎓 **学生**: student / student123

## 📁 项目结构

<details>
<summary>点击展开完整项目结构</summary>

```
📦 online-exam-system/
├── 📂 src/main/java/com/example/question/    # 🔧 后端源码
│   ├── 📂 controller/                        # 🎮 控制器层
│   │   ├── 📄 AuthController.java           # 🔐 认证控制器
│   │   ├── 📄 ExamController.java           # 📝 考试管理
│   │   ├── 📄 QuestionController.java       # ❓ 题目管理
│   │   ├── 📄 StudentController.java        # 👨‍🎓 学生管理
│   │   └── 📄 AdminController.java          # 👨‍💼 管理员控制器
│   ├── 📂 entity/                           # 🗃️ 实体类
│   │   ├── 📄 User.java                     # 👤 用户实体
│   │   ├── 📄 Exam.java                     # 📋 考试实体
│   │   ├── 📄 Question.java                 # ❓ 题目实体
│   │   └── 📄 StudentAnswer.java            # ✍️ 学生答案实体
│   ├── 📂 repository/                       # 🗄️ 数据访问层
│   │   ├── 📄 UserRepository.java           # 👤 用户数据访问
│   │   ├── 📄 ExamRepository.java           # 📋 考试数据访问
│   │   └── 📄 QuestionRepository.java       # ❓ 题目数据访问
│   ├── 📂 service/                          # 🔧 业务逻辑层
│   │   ├── 📄 AuthService.java              # 🔐 认证服务
│   │   ├── 📄 ExamService.java              # 📝 考试服务
│   │   └── 📄 QuestionService.java          # ❓ 题目服务
│   ├── 📂 config/                           # ⚙️ 配置类
│   │   └── 📄 WebConfig.java                # 🌐 Web配置
│   └── 📄 QuestionApplication.java          # 🚀 启动类
├── 📂 src/main/resources/                   # 📋 资源文件
│   ├── 📄 application.properties            # ⚙️ 应用配置
│   ├── 📄 application.properties.example    # 📝 配置模板
│   └── 📂 static/                           # 🌐 静态资源
├── 📂 vue/                                  # 🎨 前端项目
│   ├── 📂 src/
│   │   ├── 📂 components/                   # 🧩 公共组件
│   │   │   ├── 📄 Header.vue               # 🔝 页面头部
│   │   │   ├── 📄 Sidebar.vue              # 📋 侧边栏
│   │   │   └── 📄 Footer.vue               # 🔻 页面底部
│   │   ├── 📂 views/                       # 📄 页面组件
│   │   │   ├── 📄 LoginPage.vue            # 🔐 登录页面
│   │   │   ├── 📄 Dashboard.vue            # 📊 管理员仪表板
│   │   │   ├── 📄 ExamManagement.vue       # 📝 考试管理
│   │   │   ├── 📄 QuestionManagement.vue   # ❓ 题目管理
│   │   │   ├── 📄 StudentManagement.vue    # 👥 学生管理
│   │   │   ├── 📄 UserManagement.vue       # 👤 用户管理
│   │   │   ├── 📄 StudentDashboard.vue     # 🎓 学生仪表板
│   │   │   ├── 📄 ExamPage.vue             # 📝 考试页面
│   │   │   └── 📄 StudentResults.vue       # 📈 成绩查看
│   │   ├── 📂 router/                      # 🛣️ 路由配置
│   │   │   └── 📄 index.js                 # 🗺️ 路由定义
│   │   ├── 📂 config/                      # ⚙️ 配置文件
│   │   │   └── 📄 api.js                   # 🔗 API配置
│   │   ├── 📂 assets/                      # 🎨 静态资源
│   │   │   ├── 📂 css/                     # 🎨 样式文件
│   │   │   └── 📂 images/                  # 🖼️ 图片资源
│   │   └── 📄 main.js                      # 🚀 入口文件
│   ├── 📄 package.json                     # 📦 依赖配置
│   ├── 📄 vite.config.js                   # ⚡ Vite配置
│   ├── 📄 electron.js                      # 🖥️ Electron主进程
│   └── 📄 README.md                        # 📖 前端说明
├── 📂 sql/                                 # 🗄️ 数据库脚本
│   └── 📄 question_db.sql                  # 🗃️ 数据库初始化
├── 📂 docs/                                # 📚 文档目录
│   ├── 📂 images/                          # 🖼️ 截图文件
│   ├── 📄 API.md                           # 📡 API文档
│   └── 📄 DEPLOYMENT.md                    # 🚀 部署指南
├── 📄 pom.xml                              # 📦 Maven配置
├── 📄 .gitignore                           # 🚫 Git忽略文件
├── 📄 LICENSE                              # 📜 开源许可证
└── 📄 README.md                            # 📖 项目说明
```

</details>

### 📋 核心模块说明

| 模块 | 说明 | 主要功能 |
|------|------|----------|
| 🔐 **认证模块** | 用户登录认证 | JWT令牌、角色权限、会话管理 |
| 📝 **考试模块** | 考试管理核心 | 考试创建、发布、监控、结束 |
| ❓ **题目模块** | 题目管理系统 | 题目增删改查、分类、导入导出 |
| 👥 **用户模块** | 用户管理系统 | 学生、教师、管理员管理 |
| 📊 **统计模块** | 数据统计分析 | 成绩统计、考试分析、报表生成 |
| 🎨 **前端模块** | 用户界面 | 响应式设计、交互体验、桌面应用 |

## 🔧 API接口

### 认证接口
- `POST /api/v1/auth/teacher/login` - 教师登录
- `POST /api/v1/auth/student/login` - 学生登录

### 教师接口
- `GET /api/v1/teacher/questions` - 获取题目列表
- `POST /api/v1/teacher/questions` - 创建题目
- `PUT /api/v1/teacher/questions/{id}` - 更新题目
- `DELETE /api/v1/teacher/questions/{id}` - 删除题目
- `GET /api/v1/teacher/exams` - 获取考试列表
- `POST /api/v1/teacher/exams` - 创建考试
- `GET /api/v1/teacher/students` - 获取学生列表

### 学生接口
- `GET /api/v1/student/exams` - 获取可参加的考试
- `POST /api/v1/student/exams/{id}/start` - 开始考试
- `POST /api/v1/student/exams/{id}/submit` - 提交考试
- `GET /api/v1/student/exam-records/recent` - 获取考试记录

## 🎯 默认账户

### 教师账户
- 用户名: `teacher`
- 密码: `123456`

### 学生账户
- 用户名: `student1`
- 密码: `123456`

## 🔧 配置说明

### 后端配置 (application.properties)
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/question_db
spring.datasource.username=root
spring.datasource.password=your_password

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 服务器配置
server.port=8080
server.servlet.context-path=/api
```

### 前端配置 (vite.config.js)
```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 🚀 生产部署

### 后端部署

1. **打包应用**
```bash
./mvnw clean package -DskipTests
```

2. **运行JAR包**
```bash
java -jar target/question-0.0.1-SNAPSHOT.jar
```

3. **使用Docker部署**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/question-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 前端部署

1. **构建生产版本**
```bash
npm run build
```

2. **部署到Nginx**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 🐛 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库用户名密码正确
- 检查数据库是否存在

### 2. 前端无法访问后端API
- 确认后端服务已启动
- 检查端口是否被占用
- 确认跨域配置正确

### 3. 正确率显示为0%
- 确保数据库中exams表的question_count字段正确设置
- 运行以下SQL修复：
```sql
UPDATE exams SET question_count = (
    SELECT COUNT(*) FROM exam_questions 
    WHERE exam_questions.exam_id = exams.id
);
```

## 📝 开发指南

### 添加新功能
1. 后端：在对应的controller、service、repository中添加方法
2. 前端：在views或components中添加新组件
3. 更新路由配置

### 数据库迁移
1. 修改实体类
2. 运行应用，JPA会自动更新表结构
3. 或手动编写SQL脚本

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🔄 更新日志

<details>
<summary>点击查看版本更新历史</summary>

### v2.0.0 (2024-01-15)
- ✨ 新增Electron桌面应用支持
- 🎨 全新的UI设计，提升用户体验
- 🔧 优化数据库查询性能
- 🐛 修复正确率计算错误的问题
- 📱 增强移动端适配

### v1.5.0 (2023-12-20)
- ✨ 新增成绩统计和分析功能
- 🔐 增强安全性，添加JWT认证
- 📊 新增考试数据导出功能
- 🎯 优化考试界面交互体验

### v1.0.0 (2023-11-01)
- 🎉 首个正式版本发布
- 📝 完整的考试管理功能
- 👥 用户角色权限管理
- 📊 基础的成绩查看功能

</details>

## 🤝 贡献指南

我们欢迎所有形式的贡献！请阅读以下指南：

### 🔧 开发环境设置

1. **Fork 项目**
   ```bash
   # 克隆你的fork
   git clone https://github.com/your-username/Online-exam.git
   cd Online-exam
   
   # 添加上游仓库
   git remote add upstream https://github.com/xiaoxiaoguai-yyds/Online-exam.git
   ```

2. **创建功能分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **提交更改**
   ```bash
   git add .
   git commit -m "feat: 添加新功能描述"
   git push origin feature/your-feature-name
   ```

4. **创建Pull Request**

### 📝 提交规范

请使用以下格式提交代码：

- `feat:` 新功能
- `fix:` 修复bug
- `docs:` 文档更新
- `style:` 代码格式调整
- `refactor:` 代码重构
- `test:` 测试相关
- `chore:` 构建过程或辅助工具的变动

### 🐛 问题报告

发现bug？请通过以下步骤报告：

1. 检查是否已有相同问题
2. 使用问题模板创建新issue
3. 提供详细的复现步骤
4. 包含系统环境信息

## 🌟 贡献者

感谢以下贡献者对项目的支持：

<table>
<tr>
<td align="center">
<a href="https://github.com/contributor1">
<img src="https://github.com/contributor1.png" width="100px;" alt=""/>
<br />
<sub><b>贡献者1</b></sub>
</a>
</td>
<td align="center">
<a href="https://github.com/contributor2">
<img src="https://github.com/contributor2.png" width="100px;" alt=""/>
<br />
<sub><b>贡献者2</b></sub>
</a>
</td>
</tr>
</table>

## 📊 项目统计

![GitHub stars](https://img.shields.io/github/stars/xiaoxiaoguai-yyds/Online-exam?style=social)
![GitHub forks](https://img.shields.io/github/forks/xiaoxiaoguai-yyds/Online-exam?style=social)
![GitHub issues](https://img.shields.io/github/issues/xiaoxiaoguai-yyds/Online-exam)
![GitHub license](https://img.shields.io/github/license/xiaoxiaoguai-yyds/Online-exam)

## 🔗 相关链接

- 📖 [项目介绍](docs/INTRODUCTION.md) - 详细的项目背景和设计理念
- 🛠️ [开发指南](docs/DEVELOPMENT.md) - 开发环境配置和编码规范
- 🧪 [测试指南](docs/TESTING.md) - 完整的测试策略和实践
- 🚀 [部署指南](DEPLOYMENT.md) - 生产环境部署说明
- 📡 [API文档](docs/API.md) - RESTful API接口说明
- 🔒 [安全策略](SECURITY.md) - 安全漏洞报告和最佳实践
- 📝 [更新日志](CHANGELOG.md) - 版本更新记录和变更说明
- 🎥 [视频教程](https://www.youtube.com/watch?v=example) - 系统使用教程视频
- 💬 [讨论社区](https://github.com/xiaoxiaoguai-yyds/Online-exam/discussions) - 技术讨论和问题交流

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 📧 **邮箱**: your-email@example.com
- 🐛 **问题反馈**: [GitHub Issues](https://github.com/xiaoxiaoguai-yyds/Online-exam/issues)
- 💬 **讨论交流**: [GitHub Discussions](https://github.com/xiaoxiaoguai-yyds/Online-exam/discussions)
- 📱 **QQ群**: 123456789
- 💼 **微信群**: 扫描二维码加入

## 🙏 致谢

感谢以下开源项目和技术社区：

- [Spring Boot](https://spring.io/projects/spring-boot) - 强大的Java框架
- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [Element Plus](https://element-plus.org/) - 优秀的Vue 3组件库
- [MySQL](https://www.mysql.com/) - 可靠的关系型数据库
- [Electron](https://www.electronjs.org/) - 跨平台桌面应用框架

特别感谢所有为这个项目做出贡献的开发者和用户！

---

<div align="center">

**⭐ 如果这个项目对您有帮助，请给我们一个星标！**

**🔄 欢迎Fork并提交您的改进！**

**📢 分享给更多需要的人！**

---

<sub>Built with ❤️ by the Online Exam System Team</sub>

</div>
