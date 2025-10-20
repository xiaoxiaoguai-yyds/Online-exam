# 前端部署配置说明

## 接口地址配置

前端项目已经统一配置了API接口地址，方便部署时修改。

### 配置文件位置
- **主配置文件**: `src/config/api.js`
- **服务文件**: `src/services/questionApi.js`

### 修改接口地址的方法

#### 方法1: 修改配置文件（推荐）

编辑 `src/config/api.js` 文件中的生产环境配置：

```javascript
// 生产环境配置
const PROD_CONFIG = {
  // 部署到服务器时，修改这里的地址
  BASE_URL: 'http://your-server-ip:8080', // 替换为你的服务器IP或域名
  API_PREFIX: '/api'
}
```

**示例配置：**
```javascript
// 如果后端部署在 192.168.1.100:8080
const PROD_CONFIG = {
  BASE_URL: 'http://192.168.1.100:8080',
  API_PREFIX: '/api'
}

// 如果后端部署在域名 api.example.com
const PROD_CONFIG = {
  BASE_URL: 'https://api.example.com',
  API_PREFIX: '/api'
}

// 如果后端部署在同一服务器的不同端口
const PROD_CONFIG = {
  BASE_URL: 'http://localhost:8080',
  API_PREFIX: '/api'
}
```

#### 方法2: 使用环境变量

在项目根目录创建 `.env.production` 文件：

```bash
# .env.production
VUE_APP_API_BASE_URL=http://your-server-ip:8080
VUE_APP_API_PREFIX=/api
```

然后修改 `src/config/api.js`：

```javascript
// 生产环境配置
const PROD_CONFIG = {
  BASE_URL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080',
  API_PREFIX: process.env.VUE_APP_API_PREFIX || '/api'
}
```

## 前端部署步骤

### 1. 修改接口配置
按照上述方法修改接口地址配置

### 2. 构建生产版本
```bash
cd vue
npm run build
```

### 3. 部署到服务器

#### 方法A: 使用Nginx部署

1. 将 `dist` 文件夹上传到服务器
2. 配置Nginx：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/your/dist;
    index index.html;
    
    # 处理Vue Router的history模式
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 代理API请求到后端（可选）
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

#### 方法B: 使用Apache部署

1. 将 `dist` 文件夹内容复制到Apache的web目录
2. 创建 `.htaccess` 文件：

```apache
RewriteEngine On
RewriteBase /
RewriteRule ^index\.html$ - [L]
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule . /index.html [L]
```

#### 方法C: 使用Node.js服务器部署

1. 安装serve：
```bash
npm install -g serve
```

2. 启动服务：
```bash
serve -s dist -l 3000
```

### 4. 配置HTTPS（生产环境推荐）

如果使用HTTPS，需要同时配置：
- 前端使用HTTPS访问
- 后端API也使用HTTPS
- 修改配置文件中的BASE_URL为https://

## 常见问题解决

### 1. 跨域问题
如果前后端部署在不同域名/端口，需要在后端配置CORS：

```java
@CrossOrigin(origins = "http://your-frontend-domain.com")
```

### 2. 路由404问题
确保服务器配置了正确的fallback到index.html，支持Vue Router的history模式。

### 3. 静态资源路径问题
如果部署在子目录，需要配置 `vue.config.js`：

```javascript
module.exports = {
  publicPath: '/your-subdirectory/'
}
```

### 4. API请求失败
检查：
- 后端服务是否正常运行
- 接口地址配置是否正确
- 网络连接是否正常
- 防火墙是否开放相应端口

## 部署检查清单

- [ ] 修改了API配置文件中的服务器地址
- [ ] 执行了 `npm run build` 构建生产版本
- [ ] 上传了dist文件夹到服务器
- [ ] 配置了Web服务器（Nginx/Apache）
- [ ] 测试了前端页面能正常访问
- [ ] 测试了API接口能正常调用
- [ ] 配置了HTTPS（如需要）
- [ ] 设置了适当的缓存策略

## 性能优化建议

1. **启用Gzip压缩**
2. **配置静态资源缓存**
3. **使用CDN加速**
4. **开启HTTP/2**
5. **压缩图片资源**

## 监控和日志

建议配置：
- Web服务器访问日志
- 错误日志监控
- 性能监控
- 用户行为分析