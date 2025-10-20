import axios from 'axios'

// 仪表盘API接口
export const dashboardApi = {
  // 获取仪表盘统计数据
  getDashboardStats() {
    return axios.get('/api/dashboard/stats')
  },

  // 获取用户统计数据
  getUserStats() {
    return axios.get('/api/dashboard/user-stats')
  },

  // 获取考试统计数据
  getExamStats() {
    return axios.get('/api/dashboard/exam-stats')
  },

  // 获取题目统计数据
  getQuestionStats() {
    return axios.get('/api/dashboard/question-stats')
  },

  // 获取最近活动数据
  getRecentActivities() {
    return axios.get('/api/dashboard/recent-activities')
  }
}

export default dashboardApi