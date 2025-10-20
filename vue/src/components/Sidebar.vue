<template>
  <aside class="sidebar">
    <nav class="nav-menu">
      <ul>
        <li class="nav-item" :class="{ active: $route.path === '/dashboard' }">
          <router-link to="/dashboard" class="nav-link">
            <i class="icon">📊</i>
            <span>仪表盘</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: $route.path === '/questions' }">
          <router-link to="/questions" class="nav-link">
            <i class="icon">📝</i>
            <span>题目管理</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: $route.path === '/exams' }">
          <router-link to="/exams" class="nav-link">
            <i class="icon">📋</i>
            <span>考试管理</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: $route.path === '/api' }">
          <router-link to="/api" class="nav-link">
            <i class="icon">🔌</i>
            <span>接口管理</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: $route.path === '/students' }">
          <router-link to="/students" class="nav-link">
            <i class="icon">🎓</i>
            <span>学生管理</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: $route.path === '/users' }">
          <router-link to="/users" class="nav-link">
            <i class="icon">👤</i>
            <span>用户管理</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: $route.path === '/student-exams' }">
          <router-link to="/student-exams" class="nav-link">
            <i class="icon">👥</i>
            <span>学生考试</span>
          </router-link>
        </li>
        <li class="nav-item">
          <a href="#" class="nav-link">
            <i class="icon">⚙️</i>
            <span>系统设置</span>
          </a>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script>
export default {
  name: 'Sidebar'
}
</script>

<style scoped>
/* 侧边栏 */
.sidebar {
  width: 250px;
  background: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  padding: 2rem 0;
}

.nav-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin-bottom: 0.5rem;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 1rem 2rem;
  color: #64748b;
  text-decoration: none;
  transition: all 0.3s ease;
  border-right: 3px solid transparent;
}

.nav-link:hover,
.nav-item.active .nav-link {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.1) 0%, transparent 100%);
  color: #667eea;
  border-right-color: #667eea;
}

.nav-link .icon {
  margin-right: 0.75rem;
  font-size: 1.2rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    padding: 1rem 0;
  }
  
  .nav-menu ul {
    display: flex;
    overflow-x: auto;
    padding: 0 1rem;
  }
  
  .nav-item {
    margin-right: 1rem;
    margin-bottom: 0;
    white-space: nowrap;
  }
}
</style>