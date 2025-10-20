import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import StudentLoginPage from '../views/StudentLoginPage.vue'
import Dashboard from '../views/Dashboard.vue'
import QuestionManagement from '../views/QuestionManagement.vue'
import ExamManagement from '../views/ExamManagement.vue'
import ApiManagement from '../views/ApiManagement.vue'
import StudentManagement from '../views/StudentManagement.vue'
import UserManagement from '../views/UserManagement.vue'
import StudentExamResults from '../views/StudentExamResults.vue'
import ExamPage from '../views/ExamPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginPage
  },
  {
    path: '/student/login',
    name: 'StudentLogin',
    component: StudentLoginPage
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/student/dashboard',
    name: 'StudentDashboard',
    component: () => import('../views/StudentDashboard.vue'),
    meta: { requiresAuth: true, userType: 'student' }
  },
  {
    path: '/questions',
    name: 'QuestionManagement',
    component: QuestionManagement,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/exams',
    name: 'ExamManagement',
    component: ExamManagement,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/api',
    name: 'ApiManagement',
    component: ApiManagement,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/students',
    name: 'StudentManagement',
    component: StudentManagement,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/student-exams',
    name: 'StudentExamResults',
    component: StudentExamResults,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/student/exam/:id',
    name: 'ExamPage',
    component: ExamPage,
    meta: { requiresAuth: true, userType: 'student' }
  },
  {
    path: '/student/results',
    name: 'StudentResults',
    component: () => import('../views/StudentResults.vue'),
    meta: { requiresAuth: true, userType: 'student' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  const adminToken = localStorage.getItem('token')
  const studentToken = localStorage.getItem('studentToken')
  const userType = localStorage.getItem('userType')
  
  if (to.meta.requiresAuth) {
    // 需要认证的路由
    if (to.meta.userType === 'admin' && !adminToken) {
      // 管理员页面但没有管理员token
      next('/login')
    } else if (to.meta.userType === 'student' && !studentToken) {
      // 学生页面但没有学生token
      next('/student/login')
    } else if (to.meta.userType === 'admin' && userType !== 'admin') {
      // 学生用户访问管理员页面
      next('/student/dashboard')
    } else if (to.meta.userType === 'student' && userType !== 'student') {
      // 管理员用户访问学生页面
      next('/dashboard')
    } else {
      next()
    }
  } else if (to.path === '/login' && adminToken && userType === 'admin') {
    // 已登录的管理员访问登录页，跳转到管理员仪表盘
    next('/dashboard')
  } else if (to.path === '/student/login' && studentToken && userType === 'student') {
    // 已登录的学生访问学生登录页，跳转到学生仪表盘
    next('/student/dashboard')
  } else {
    next()
  }
})

export default router