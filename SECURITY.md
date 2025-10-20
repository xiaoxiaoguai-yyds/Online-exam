# 🛡️ 安全策略

## 📋 支持的版本

我们目前支持以下版本的安全更新：

| 版本 | 支持状态 |
| ------- | ------------------ |
| 2.0.x   | ✅ 支持 |
| 1.5.x   | ✅ 支持 |
| < 1.5   | ❌ 不支持 |

## 🚨 报告安全漏洞

我们非常重视安全问题。如果您发现了安全漏洞，请**不要**通过公开的GitHub issues报告。

### 📧 私密报告

请通过以下方式私密报告安全漏洞：

- **邮箱**: security@example.com
- **主题**: [SECURITY] 在线考试系统安全漏洞报告

### 📝 报告内容

请在报告中包含以下信息：

1. **漏洞描述**: 详细描述安全漏洞
2. **影响范围**: 说明漏洞可能造成的影响
3. **复现步骤**: 提供详细的复现步骤
4. **环境信息**: 包括版本号、操作系统等
5. **修复建议**: 如果有修复建议请一并提供

### ⏰ 响应时间

- **确认收到**: 24小时内
- **初步评估**: 72小时内
- **详细分析**: 1周内
- **修复发布**: 根据严重程度，1-4周内

## 🔒 安全最佳实践

### 🔐 认证和授权

- 使用强密码策略
- 启用JWT令牌过期机制
- 实施角色基础的访问控制
- 定期轮换密钥和令牌

### 🗄️ 数据库安全

- 使用参数化查询防止SQL注入
- 限制数据库用户权限
- 启用数据库连接加密
- 定期备份数据库

### 🌐 网络安全

- 使用HTTPS加密传输
- 配置CORS策略
- 实施请求频率限制
- 启用安全头部

### 📊 日志和监控

- 记录所有认证尝试
- 监控异常访问模式
- 定期审查访问日志
- 设置安全告警

## 🔧 安全配置

### 生产环境配置

```properties
# 数据库安全
spring.datasource.url=jdbc:mysql://localhost:3306/question_db?useSSL=true&requireSSL=true
spring.jpa.show-sql=false

# 会话安全
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true

# 安全头部
security.headers.frame=DENY
security.headers.content-type=nosniff
security.headers.xss=1; mode=block
```

### JWT配置

```properties
# JWT安全配置
jwt.secret=your-very-long-and-secure-secret-key
jwt.expiration=3600000
jwt.refresh-expiration=86400000
```

## 🚫 已知安全限制

1. **文件上传**: 当前版本不支持文件上传功能
2. **密码重置**: 需要管理员手动重置
3. **多因素认证**: 计划在下个版本中实现

## 📚 安全资源

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security文档](https://spring.io/projects/spring-security)
- [Vue.js安全指南](https://vuejs.org/guide/best-practices/security.html)

## 🏆 安全致谢

感谢以下安全研究人员的贡献：

- 暂无

如果您报告了安全漏洞并希望被列入致谢名单，请在报告中说明。

---

**注意**: 本安全策略会定期更新，请关注最新版本。