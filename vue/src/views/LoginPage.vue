<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="background-animation">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>
    
    <!-- 主登录卡片 -->
    <div class="login-card">
      <!-- 左侧装饰区域 -->
      <div class="login-decoration">
        <div class="decoration-content">
          <h1 class="brand-title">Question</h1>
          <p class="brand-subtitle">智能问答系统</p>
          <div class="decoration-graphic">
            <svg viewBox="0 0 200 200" class="graphic-svg">
              <defs>
                <linearGradient id="gradient1" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#667eea;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#764ba2;stop-opacity:1" />
                </linearGradient>
              </defs>
              <circle cx="100" cy="100" r="80" fill="url(#gradient1)" opacity="0.8">
                <animate attributeName="r" values="80;90;80" dur="3s" repeatCount="indefinite"/>
              </circle>
              <circle cx="100" cy="100" r="60" fill="none" stroke="white" stroke-width="2" opacity="0.6">
                <animate attributeName="r" values="60;70;60" dur="2s" repeatCount="indefinite"/>
              </circle>
              <text x="100" y="110" text-anchor="middle" fill="white" font-size="24" font-weight="bold">?</text>
            </svg>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-form-section">
        <div class="form-container">
          <div class="form-header">
            <h2 class="form-title">欢迎回来</h2>
            <p class="form-subtitle">请登录您的账户</p>
          </div>
          
          <form @submit.prevent="handleLogin" class="login-form">
            <!-- 用户名输入框 -->
            <div class="input-group">
              <div class="input-wrapper" :class="{ 'focused': focusedField === 'username', 'error': errors.username }">
                <svg class="input-icon" viewBox="0 0 24 24">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                <input 
                  type="text" 
                  v-model="loginForm.username" 
                  placeholder="用户名或邮箱"
                  @focus="focusedField = 'username'"
                  @blur="focusedField = ''"
                  :class="{ 'error': errors.username }"
                  required
                >
                <div class="input-border"></div>
              </div>
              <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
            </div>
            
            <!-- 密码输入框 -->
            <div class="input-group">
              <div class="input-wrapper" :class="{ 'focused': focusedField === 'password', 'error': errors.password }">
                <svg class="input-icon" viewBox="0 0 24 24">
                  <path d="M18,8h-1V6c0-2.76-2.24-5-5-5S7,3.24,7,6v2H6c-1.1,0-2,0.9-2,2v10c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V10C20,8.9,19.1,8,18,8z M12,17c-1.1,0-2-0.9-2-2s0.9-2,2-2s2,0.9,2,2S13.1,17,12,17z M15.1,8H8.9V6c0-1.71,1.39-3.1,3.1-3.1s3.1,1.39,3.1,3.1V8z"/>
                </svg>
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  v-model="loginForm.password" 
                  placeholder="密码"
                  @focus="focusedField = 'password'"
                  @blur="focusedField = ''"
                  :class="{ 'error': errors.password }"
                  required
                >
                <button type="button" class="password-toggle" @click="showPassword = !showPassword">
                  <svg v-if="showPassword" viewBox="0 0 24 24">
                    <path d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17,19.5 21.27,16.39 23,12C21.27,7.61 17,4.5 12,4.5Z"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24">
                    <path d="M11.83,9L15,12.16C15,12.11 15,12.05 15,12A3,3 0 0,0 12,9C11.94,9 11.89,9 11.83,9M7.53,9.8L9.08,11.35C9.03,11.56 9,11.77 9,12A3,3 0 0,0 12,15C12.22,15 12.44,14.97 12.65,14.92L14.2,16.47C13.53,16.8 12.79,17 12,17A5,5 0 0,1 7,12C7,11.21 7.2,10.47 7.53,9.8M2,4.27L4.28,6.55L4.73,7C3.08,8.3 1.78,10 1,12C2.73,16.39 7,19.5 12,19.5C13.55,19.5 15.03,19.2 16.38,18.66L16.81,19.09L19.73,22L21,20.73L3.27,3M12,7A5,5 0 0,1 17,12C17,12.64 16.87,13.26 16.64,13.82L19.57,16.75C21.07,15.5 22.27,13.86 23,12C21.27,7.61 17,4.5 12,4.5C10.6,4.5 9.26,4.75 8,5.2L10.17,7.35C10.76,7.13 11.37,7 12,7Z"/>
                  </svg>
                </button>
                <div class="input-border"></div>
              </div>
              <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            </div>
            
            <!-- 记住我选项 -->
            <div class="form-options">
              <label class="checkbox-wrapper">
                <input type="checkbox" v-model="loginForm.rememberMe">
                <span class="checkmark"></span>
                <span class="checkbox-label">记住我</span>
              </label>
              <a href="#" class="forgot-password">忘记密码？</a>
            </div>
            
            <!-- 登录按钮 -->
            <button type="submit" class="login-button" :disabled="loading" :class="{ 'loading': loading }">
              <span v-if="!loading" class="button-text">登录</span>
              <div v-else class="loading-spinner">
                <div class="spinner"></div>
                <span>登录中...</span>
              </div>
            </button>
            
            <!-- 错误提示 -->
            <div v-if="loginError" class="error-alert">
              <svg class="error-icon" viewBox="0 0 24 24">
                <path d="M13,13H11V7H13M13,17H11V15H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
              </svg>
              {{ loginError }}
            </div>
            
            <!-- 成功提示 -->
            <div v-if="loginSuccess" class="success-alert">
              <svg class="success-icon" viewBox="0 0 24 24">
                <path d="M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M11,16.5L18,9.5L16.59,8.09L11,13.67L7.91,10.59L6.5,12L11,16.5Z"/>
              </svg>
              {{ loginSuccess }}
            </div>
          </form>
          
          <!-- 注册链接和学生登录 -->
          <div class="form-footer">
            <p>还没有账户？ <a href="#" class="register-link">立即注册</a></p>
            <div class="student-login-section">
              <p>学生用户？ <router-link to="/student/login" class="student-login-link">学生登录</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginPage',
  setup() {
    const router = useRouter()
    return { router }
  },
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        rememberMe: false
      },
      focusedField: '',
      showPassword: false,
      loading: false,
      loginError: '',
      loginSuccess: '',
      errors: {}
    }
  },
  methods: {
    async handleLogin() {
      // 清除之前的错误
      this.errors = {}
      this.loginError = ''
      this.loginSuccess = ''
      
      // 表单验证
      if (!this.loginForm.username.trim()) {
        this.errors.username = '请输入用户名或邮箱'
        return
      }
      
      if (!this.loginForm.password.trim()) {
        this.errors.password = '请输入密码'
        return
      }
      
      if (this.loginForm.password.length < 6) {
        this.errors.password = '密码长度至少6位'
        return
      }
      
      this.loading = true
      
      try {
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          username: this.loginForm.username,
          password: this.loginForm.password,
          rememberMe: this.loginForm.rememberMe
        })
        
        if (response.data.code === 200) {
          this.loginSuccess = '登录成功！正在跳转...'
          
          // 保存用户信息到localStorage
          localStorage.setItem('token', response.data.data.token)
          localStorage.setItem('userInfo', JSON.stringify(response.data.data))
          localStorage.setItem('userType', 'admin')
          
          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            console.log('登录成功，用户信息：', response.data.data)
            this.router.push('/dashboard')
          }, 1500)
        } else {
          this.loginError = response.data.message || '登录失败'
        }
      } catch (error) {
        console.error('登录错误：', error)
        if (error.response && error.response.data) {
          this.loginError = error.response.data.message || '登录失败，请检查网络连接'
        } else {
          this.loginError = '网络连接失败，请稍后重试'
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
/* 主容器样式 */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景动画 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 20%;
  right: 10%;
  animation-delay: 1s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 30%;
  left: 20%;
  animation-delay: 2s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  bottom: 10%;
  right: 20%;
  animation-delay: 3s;
}

.shape-5 {
  width: 40px;
  height: 40px;
  top: 50%;
  left: 5%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 登录卡片 */
.login-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  max-width: 900px;
  width: 100%;
  min-height: 600px;
  position: relative;
  z-index: 2;
}

/* 左侧装饰区域 */
.login-decoration {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
  position: relative;
}

.decoration-content {
  text-align: center;
  z-index: 2;
}

.brand-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.brand-subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 30px;
}

.decoration-graphic {
  width: 200px;
  height: 200px;
  margin: 0 auto;
}

.graphic-svg {
  width: 100%;
  height: 100%;
}

/* 右侧表单区域 */
.login-form-section {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-container {
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-title {
  font-size: 2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.form-subtitle {
  color: #666;
  font-size: 1rem;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

.input-group {
  margin-bottom: 25px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.input-wrapper.focused {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-wrapper.error {
  border-color: #e74c3c;
  background: #fdf2f2;
}

.input-icon {
  width: 20px;
  height: 20px;
  margin: 0 15px;
  fill: #999;
  transition: fill 0.3s ease;
}

.input-wrapper.focused .input-icon {
  fill: #667eea;
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 15px 0;
  font-size: 1rem;
  color: #333;
  outline: none;
}

.input-wrapper input::placeholder {
  color: #999;
}

.password-toggle {
  background: none;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.password-toggle svg {
  width: 20px;
  height: 20px;
  fill: #999;
  transition: fill 0.3s ease;
}

.password-toggle:hover svg {
  fill: #667eea;
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: #667eea;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.input-wrapper.focused .input-border {
  width: 100%;
}

.error-message {
  color: #e74c3c;
  font-size: 0.875rem;
  margin-top: 5px;
  display: block;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.checkbox-wrapper input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #ddd;
  border-radius: 4px;
  margin-right: 8px;
  position: relative;
  transition: all 0.3s ease;
}

.checkbox-wrapper input[type="checkbox"]:checked + .checkmark {
  background: #667eea;
  border-color: #667eea;
}

.checkbox-wrapper input[type="checkbox"]:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 2px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label {
  color: #666;
  font-size: 0.9rem;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #5a67d8;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 15px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.button-text {
  display: block;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 提示信息 */
.error-alert, .success-alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 8px;
  margin-top: 20px;
  font-size: 0.9rem;
}

.error-alert {
  background: #fdf2f2;
  color: #e74c3c;
  border: 1px solid #fadbd8;
}

.success-alert {
  background: #f0f9f0;
  color: #27ae60;
  border: 1px solid #d5f4d5;
}

.error-icon, .success-icon {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

/* 表单底部 */
.form-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.form-footer p {
  color: #666;
  margin: 0;
}

.register-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #5a67d8;
}

.student-login-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.student-login-link {
  color: #4facfe;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.student-login-link:hover {
  color: #00f2fe;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    max-width: 400px;
    margin: 20px;
  }
  
  .login-decoration {
    padding: 30px 20px;
  }
  
  .brand-title {
    font-size: 2rem;
  }
  
  .decoration-graphic {
    width: 150px;
    height: 150px;
  }
  
  .login-form-section {
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .form-title {
    font-size: 1.5rem;
  }
  
  .brand-title {
    font-size: 1.8rem;
  }
}
</style>