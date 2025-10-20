<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <header class="layout-header">
      <div class="header-content">
        <h1 class="logo">后台管理系统</h1>
        <div class="user-info">
          <span class="welcome-text">欢迎，{{ userInfo.username }}</span>
          <button @click="logout" class="logout-btn">退出登录</button>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="layout-main">
      <!-- 侧边栏 -->
      <Sidebar />

      <!-- 内容区域 -->
      <section class="layout-content">
        <slot></slot>
      </section>
    </main>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from './Sidebar.vue'

export default {
  name: 'Layout',
  components: {
    Sidebar
  },
  setup() {
    const router = useRouter()
    const userInfo = ref({
      username: '管理员'
    })

    onMounted(() => {
      // 从localStorage获取用户信息
      const storedUserInfo = localStorage.getItem('userInfo')
      if (storedUserInfo) {
        const parsed = JSON.parse(storedUserInfo)
        userInfo.value.username = parsed.username || '管理员'
      }
    })

    const logout = () => {
      // 清除本地存储的用户信息
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      
      // 跳转到登录页面
      router.push('/login')
    }

    return {
      userInfo,
      logout
    }
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 顶部导航栏 */
.layout-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.logo {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome-text {
  font-size: 0.9rem;
  opacity: 0.9;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 主要内容区域 */
.layout-main {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 80px);
}

/* 内容区域 */
.layout-content {
  flex: 1;
  padding: 2rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-main {
    flex-direction: column;
  }
  
  .layout-content {
    padding: 1rem;
  }
}
</style>