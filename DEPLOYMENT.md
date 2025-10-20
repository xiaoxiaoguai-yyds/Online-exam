# 部署指南

本文档详细说明了如何在不同环境中部署在线考试系统。

## 📋 目录

- [开发环境部署](#开发环境部署)
- [生产环境部署](#生产环境部署)
- [Docker部署](#docker部署)
- [云服务器部署](#云服务器部署)
- [常见问题](#常见问题)

## 🔧 开发环境部署

### 前置要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/question-management-system.git
cd question-management-system
```

2. **配置数据库**
```sql
CREATE DATABASE question_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **配置应用**
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
# 编辑 application.properties 文件，修改数据库连接信息
```

4. **启动后端**
```bash
./mvnw spring-boot:run
```

5. **启动前端**
```bash
cd vue
npm install
npm run dev
```

## 🚀 生产环境部署

### 方式一：传统部署

#### 后端部署

1. **打包应用**
```bash
./mvnw clean package -DskipTests
```

2. **创建生产配置**
```bash
cp src/main/resources/application.properties src/main/resources/application-prod.properties
```

编辑 `application-prod.properties`：
```properties
# 生产环境数据库配置
spring.datasource.url=jdbc:mysql://your-prod-host:3306/question_db
spring.datasource.username=prod_user
spring.datasource.password=secure_password

# 关闭SQL日志
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# 生产环境端口
server.port=8080
```

3. **运行应用**
```bash
java -jar -Dspring.profiles.active=prod target/question-0.0.1-SNAPSHOT.jar
```

#### 前端部署

1. **构建生产版本**
```bash
cd vue
npm run build
```

2. **配置Nginx**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/vue/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 方式二：使用systemd服务

1. **创建服务文件**
```bash
sudo nano /etc/systemd/system/question-system.service
```

```ini
[Unit]
Description=Question Management System
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/question-management-system
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod target/question-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

2. **启动服务**
```bash
sudo systemctl daemon-reload
sudo systemctl enable question-system
sudo systemctl start question-system
```

## 🐳 Docker部署

### 创建Dockerfile

**后端Dockerfile**
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/question-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**前端Dockerfile**
```dockerfile
FROM node:16-alpine as build

WORKDIR /app
COPY vue/package*.json ./
RUN npm install

COPY vue/ .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

### Docker Compose

创建 `docker-compose.yml`：
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: question_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/question_db.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/question_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword

  frontend:
    build:
      context: .
      dockerfile: Dockerfile.frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### 运行Docker Compose

```bash
docker-compose up -d
```

## ☁️ 云服务器部署

### 阿里云ECS部署

1. **购买ECS实例**
   - 选择Ubuntu 20.04或CentOS 8
   - 配置安全组，开放80、8080、3306端口

2. **安装环境**
```bash
# 安装JDK
sudo apt update
sudo apt install openjdk-17-jdk

# 安装MySQL
sudo apt install mysql-server

# 安装Node.js
curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt-get install -y nodejs

# 安装Nginx
sudo apt install nginx
```

3. **部署应用**
按照生产环境部署步骤进行

### 腾讯云CVM部署

类似阿里云ECS，选择合适的实例规格和操作系统。

## 🔒 安全配置

### 数据库安全

1. **创建专用数据库用户**
```sql
CREATE USER 'question_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON question_db.* TO 'question_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **配置防火墙**
```bash
# Ubuntu
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 22
sudo ufw enable

# CentOS
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --reload
```

### SSL证书配置

使用Let's Encrypt免费证书：
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

## 📊 监控和日志

### 应用监控

1. **配置Spring Boot Actuator**
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

2. **查看应用状态**
```bash
curl http://localhost:8080/api/actuator/health
```

### 日志管理

1. **配置logback**
创建 `src/main/resources/logback-spring.xml`：
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/question-system.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/question-system.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## 🔧 常见问题

### 1. 端口被占用

```bash
# 查看端口占用
sudo netstat -tlnp | grep :8080

# 杀死进程
sudo kill -9 <PID>
```

### 2. 数据库连接失败

- 检查MySQL服务状态：`sudo systemctl status mysql`
- 检查防火墙设置
- 验证数据库用户权限

### 3. 前端无法访问后端

- 检查Nginx配置
- 确认代理设置正确
- 检查CORS配置

### 4. 内存不足

```bash
# 设置JVM内存参数
java -Xms512m -Xmx1024m -jar app.jar
```

### 5. 文件权限问题

```bash
# 设置正确的文件权限
sudo chown -R your-user:your-user /path/to/application
sudo chmod +x /path/to/application/mvnw
```

## 📈 性能优化

### 数据库优化

1. **添加索引**
```sql
CREATE INDEX idx_exam_student ON exam_records(exam_id, student_id);
CREATE INDEX idx_question_exam ON exam_questions(exam_id);
```

2. **配置连接池**
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

### 应用优化

1. **启用缓存**
```properties
spring.cache.type=simple
```

2. **配置JVM参数**
```bash
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar app.jar
```

## 📞 技术支持

如果在部署过程中遇到问题，请：

1. 查看应用日志：`tail -f logs/question-system.log`
2. 检查系统资源：`htop` 或 `top`
3. 提交Issue：[GitHub Issues](https://github.com/your-username/question-management-system/issues)

---

更新时间：2024年1月