# 🛠️ 开发指南

## 📋 开发环境准备

### 必需软件

| 软件 | 版本 | 用途 | 下载地址 |
|------|------|------|----------|
| JDK | 17+ | Java开发环境 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| Node.js | 16+ | 前端开发环境 | [Node.js](https://nodejs.org/) |
| MySQL | 8.0+ | 数据库 | [MySQL](https://dev.mysql.com/downloads/) |
| Maven | 3.8+ | Java构建工具 | [Maven](https://maven.apache.org/) |
| Git | 最新版 | 版本控制 | [Git](https://git-scm.com/) |

### 推荐IDE

| IDE | 用途 | 插件推荐 |
|-----|------|----------|
| IntelliJ IDEA | Java后端开发 | Spring Boot, Lombok, MyBatis |
| VS Code | 前端开发 | Vue Language Features, ESLint, Prettier |

## 🚀 项目启动

### 1. 克隆项目
```bash
git clone https://github.com/xiaoxiaoguai-yyds/Online-exam.git
cd Online-exam
```

### 2. 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE question_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据
mysql -u root -p question_db < sql/question_db.sql
```

### 3. 后端启动
```bash
# 配置数据库连接
cp src/main/resources/application.properties.example src/main/resources/application.properties
# 编辑 application.properties 文件，配置数据库连接信息

# 启动后端服务
mvn spring-boot:run
```

### 4. 前端启动
```bash
cd vue
npm install
npm run dev
```

## 📁 项目结构详解

### 后端结构
```
src/main/java/com/example/question/
├── controller/          # 控制器层
│   ├── AuthController.java      # 认证相关接口
│   ├── ExamController.java      # 考试管理接口
│   ├── QuestionController.java  # 题目管理接口
│   └── StudentController.java   # 学生相关接口
├── entity/             # 实体类
│   ├── User.java               # 用户实体
│   ├── Exam.java               # 考试实体
│   ├── Question.java           # 题目实体
│   └── StudentAnswer.java      # 学生答案实体
├── repository/         # 数据访问层
│   ├── UserRepository.java     # 用户数据访问
│   ├── ExamRepository.java     # 考试数据访问
│   └── QuestionRepository.java # 题目数据访问
├── service/            # 业务逻辑层
│   ├── AuthService.java        # 认证服务
│   ├── ExamService.java        # 考试服务
│   └── QuestionService.java    # 题目服务
└── config/             # 配置类
    └── WebConfig.java          # Web配置
```

### 前端结构
```
vue/src/
├── components/         # 公共组件
│   ├── Header.vue     # 页面头部
│   ├── Sidebar.vue    # 侧边栏
│   └── Footer.vue     # 页面底部
├── views/             # 页面组件
│   ├── LoginPage.vue          # 登录页面
│   ├── Dashboard.vue          # 管理员仪表板
│   ├── ExamManagement.vue     # 考试管理
│   ├── QuestionManagement.vue # 题目管理
│   ├── StudentManagement.vue  # 学生管理
│   ├── ExamPage.vue           # 考试页面
│   └── StudentResults.vue     # 成绩查看
├── router/            # 路由配置
│   └── index.js       # 路由定义
├── config/            # 配置文件
│   └── api.js         # API配置
└── assets/            # 静态资源
    ├── css/           # 样式文件
    └── images/        # 图片资源
```

## 🔧 开发规范

### 代码规范

#### Java代码规范
```java
// 类名使用大驼峰命名
public class ExamController {
    
    // 方法名使用小驼峰命名
    public ResponseEntity<List<Exam>> getAllExams() {
        // 方法实现
    }
    
    // 常量使用大写字母和下划线
    private static final String DEFAULT_PAGE_SIZE = "10";
}
```

#### Vue代码规范
```vue
<template>
  <!-- 使用kebab-case命名组件 -->
  <exam-management />
</template>

<script>
export default {
  name: 'ExamManagement', // 组件名使用PascalCase
  data() {
    return {
      examList: [], // 变量名使用camelCase
      isLoading: false
    }
  },
  methods: {
    // 方法名使用camelCase
    fetchExamList() {
      // 方法实现
    }
  }
}
</script>
```

### Git提交规范

使用约定式提交格式：

```bash
# 功能开发
git commit -m "feat: 添加考试管理功能"

# Bug修复
git commit -m "fix: 修复登录验证问题"

# 文档更新
git commit -m "docs: 更新API文档"

# 代码重构
git commit -m "refactor: 重构用户服务层代码"

# 性能优化
git commit -m "perf: 优化数据库查询性能"

# 测试相关
git commit -m "test: 添加用户管理单元测试"
```

### 分支管理

```bash
# 主分支
main          # 生产环境代码
develop       # 开发环境代码

# 功能分支
feature/exam-management    # 考试管理功能
feature/user-auth         # 用户认证功能

# 修复分支
hotfix/login-bug          # 登录bug修复
hotfix/security-patch     # 安全补丁

# 发布分支
release/v1.0.0           # 版本发布
```

## 🧪 测试指南

### 单元测试

#### 后端测试
```java
@SpringBootTest
class ExamServiceTest {
    
    @Autowired
    private ExamService examService;
    
    @Test
    void testCreateExam() {
        // 测试创建考试功能
        Exam exam = new Exam();
        exam.setTitle("测试考试");
        
        Exam savedExam = examService.createExam(exam);
        
        assertThat(savedExam.getId()).isNotNull();
        assertThat(savedExam.getTitle()).isEqualTo("测试考试");
    }
}
```

#### 前端测试
```javascript
import { mount } from '@vue/test-utils'
import ExamManagement from '@/views/ExamManagement.vue'

describe('ExamManagement.vue', () => {
  it('renders exam list correctly', () => {
    const wrapper = mount(ExamManagement)
    expect(wrapper.find('.exam-list').exists()).toBe(true)
  })
})
```

### 集成测试

```bash
# 运行后端测试
mvn test

# 运行前端测试
cd vue
npm run test

# 运行端到端测试
npm run test:e2e
```

## 🔍 调试技巧

### 后端调试

1. **使用IDE断点调试**
   - 在关键代码行设置断点
   - 使用Debug模式启动应用
   - 逐步执行代码，观察变量值

2. **日志调试**
   ```java
   @Slf4j
   public class ExamService {
       public Exam createExam(Exam exam) {
           log.debug("Creating exam: {}", exam.getTitle());
           // 业务逻辑
           log.info("Exam created successfully with id: {}", exam.getId());
           return exam;
       }
   }
   ```

3. **使用Spring Boot Actuator**
   ```properties
   # application.properties
   management.endpoints.web.exposure.include=health,info,metrics
   ```

### 前端调试

1. **浏览器开发者工具**
   - 使用Console查看日志
   - 使用Network监控API请求
   - 使用Vue DevTools调试组件状态

2. **Vue调试**
   ```javascript
   export default {
     methods: {
       fetchData() {
         console.log('Fetching data...')
         this.$http.get('/api/exams')
           .then(response => {
             console.log('Response:', response.data)
             this.examList = response.data
           })
           .catch(error => {
             console.error('Error:', error)
           })
       }
     }
   }
   ```

## 📦 构建和部署

### 开发环境构建

```bash
# 后端开发环境
mvn spring-boot:run

# 前端开发环境
cd vue
npm run dev
```

### 生产环境构建

```bash
# 后端生产构建
mvn clean package -Pprod

# 前端生产构建
cd vue
npm run build

# Electron桌面应用构建
npm run electron:build
```

### Docker部署

```dockerfile
# 后端Dockerfile
FROM openjdk:17-jdk-slim
COPY target/question-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```dockerfile
# 前端Dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

## 🐛 常见问题

### 后端常见问题

1. **数据库连接失败**
   ```
   解决方案：
   - 检查数据库服务是否启动
   - 验证连接配置是否正确
   - 确认数据库用户权限
   ```

2. **端口占用**
   ```bash
   # 查找占用端口的进程
   netstat -ano | findstr :8080
   
   # 杀死进程
   taskkill /PID <进程ID> /F
   ```

### 前端常见问题

1. **依赖安装失败**
   ```bash
   # 清除缓存重新安装
   npm cache clean --force
   rm -rf node_modules
   npm install
   ```

2. **跨域问题**
   ```javascript
   // vite.config.js
   export default {
     server: {
       proxy: {
         '/api': {
           target: 'http://localhost:8080',
           changeOrigin: true
         }
       }
     }
   }
   ```

## 📚 学习资源

### 官方文档
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Vue 3官方文档](https://vuejs.org/)
- [Element Plus文档](https://element-plus.org/)

### 推荐教程
- [Spring Boot实战](https://spring.io/guides)
- [Vue 3深入指南](https://vuejs.org/guide/)
- [MySQL性能优化](https://dev.mysql.com/doc/)

### 社区资源
- [Stack Overflow](https://stackoverflow.com/)
- [GitHub](https://github.com/)
- [掘金](https://juejin.cn/)

---

*如有其他开发问题，欢迎在GitHub Issues中提出。*