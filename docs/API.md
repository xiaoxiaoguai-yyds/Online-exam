# 📡 API 接口文档

## 🔐 认证接口

### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "userType": "admin|teacher|student"
}
```

**响应示例:**
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "userType": "admin",
      "name": "管理员"
    }
  }
}
```

## 👥 用户管理接口

### 获取用户列表
```http
GET /api/admin/users?page=1&size=10&userType=student
Authorization: Bearer {token}
```

### 创建用户
```http
POST /api/admin/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "student001",
  "password": "123456",
  "name": "张三",
  "userType": "student",
  "email": "student001@example.com"
}
```

## 📝 考试管理接口

### 获取考试列表
```http
GET /api/exams?page=1&size=10&status=published
Authorization: Bearer {token}
```

### 创建考试
```http
POST /api/exams
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "期末考试",
  "description": "2024年春季期末考试",
  "startTime": "2024-06-01T09:00:00",
  "endTime": "2024-06-01T11:00:00",
  "duration": 120,
  "questionIds": [1, 2, 3, 4, 5]
}
```

### 发布考试
```http
PUT /api/exams/{examId}/publish
Authorization: Bearer {token}
```

## ❓ 题目管理接口

### 获取题目列表
```http
GET /api/questions?page=1&size=10&type=single_choice
Authorization: Bearer {token}
```

### 创建题目
```http
POST /api/questions
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "以下哪个是Java的关键字？",
  "type": "single_choice",
  "options": ["class", "Class", "CLASS", "以上都是"],
  "correctAnswer": "A",
  "score": 5,
  "difficulty": "easy"
}
```

## 🎓 学生考试接口

### 获取可参加的考试
```http
GET /api/student/exams/available
Authorization: Bearer {token}
```

### 开始考试
```http
POST /api/student/exams/{examId}/start
Authorization: Bearer {token}
```

### 提交答案
```http
POST /api/student/exams/{examId}/submit
Authorization: Bearer {token}
Content-Type: application/json

{
  "answers": [
    {
      "questionId": 1,
      "answer": "A"
    },
    {
      "questionId": 2,
      "answer": "B,C"
    }
  ]
}
```

### 获取考试结果
```http
GET /api/student/exams/{examId}/result
Authorization: Bearer {token}
```

## 📊 统计分析接口

### 获取考试统计
```http
GET /api/admin/statistics/exam/{examId}
Authorization: Bearer {token}
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "examId": 1,
    "examTitle": "期末考试",
    "totalStudents": 50,
    "submittedCount": 45,
    "averageScore": 78.5,
    "passRate": 0.85,
    "scoreDistribution": {
      "90-100": 8,
      "80-89": 15,
      "70-79": 12,
      "60-69": 7,
      "0-59": 3
    }
  }
}
```

## 🔧 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数格式 |
| 401 | 未授权访问 | 检查token是否有效 |
| 403 | 权限不足 | 检查用户权限 |
| 404 | 资源不存在 | 检查请求路径 |
| 500 | 服务器内部错误 | 联系管理员 |

## 📝 请求头说明

所有需要认证的接口都需要在请求头中包含：

```http
Authorization: Bearer {your_jwt_token}
Content-Type: application/json
```

## 🔄 分页参数

大部分列表接口支持分页参数：

- `page`: 页码，从1开始
- `size`: 每页数量，默认10
- `sort`: 排序字段，如 `createTime,desc`

## 📱 响应格式

所有接口统一返回格式：

```json
{
  "success": boolean,
  "message": "string",
  "data": object,
  "timestamp": "2024-01-15T10:30:00Z"
}
```