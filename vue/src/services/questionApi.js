import axios from 'axios'
import { API_CONFIG } from '../config/api.js'

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加认证token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // token过期或无效，跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

/**
 * 题目管理API服务
 */
export const questionApi = {
  /**
   * 获取题目列表（分页查询）
   * @param {Object} params 查询参数
   * @param {number} params.page 页码（从0开始）
   * @param {number} params.size 每页大小
   * @param {string} params.keyword 搜索关键词
   * @param {string} params.type 题目类型
   * @param {string} params.difficulty 难度等级
   * @returns {Promise} API响应
   */
  async getQuestions(params = {}) {
    console.log('=== questionApi.getQuestions 开始 ===')
    console.log('请求参数:', JSON.stringify(params, null, 2))
    console.log('API基础URL:', API_CONFIG.BASE_URL)
    console.log('完整请求URL:', `${API_CONFIG.BASE_URL}/questions`)
    
    try {
      console.log('发送GET请求到 /questions')
      const startTime = Date.now()
      
      const response = await apiClient.get('/api/questions', { params })
      
      const endTime = Date.now()
      console.log('请求耗时:', (endTime - startTime) + 'ms')
      console.log('HTTP状态码:', response.status)
      console.log('HTTP状态文本:', response.statusText)
      console.log('响应头:', response.headers)
      console.log('响应数据:', JSON.stringify(response.data, null, 2))
      console.log('=== questionApi.getQuestions 成功 ===')
      
      return response.data
    } catch (error) {
      console.error('=== questionApi.getQuestions 失败 ===')
      console.error('错误类型:', error.name)
      console.error('错误消息:', error.message)
      
      if (error.response) {
        console.error('HTTP响应错误:')
        console.error('- 状态码:', error.response.status)
        console.error('- 状态文本:', error.response.statusText)
        console.error('- 响应头:', error.response.headers)
        console.error('- 响应数据:', error.response.data)
      } else if (error.request) {
        console.error('网络请求错误:')
        console.error('- 请求配置:', error.config)
        console.error('- 请求对象:', error.request)
        console.error('- 可能原因: 服务器未启动或网络连接问题')
      } else {
        console.error('请求配置错误:', error.message)
      }
      
      console.error('完整错误对象:', error)
      throw error
    }
  },

  /**
   * 根据ID获取题目详情
   * @param {number} id 题目ID
   * @returns {Promise} API响应
   */
  async getQuestionById(id) {
    try {
      const response = await apiClient.get(`/api/questions/${id}`)
      return response.data
    } catch (error) {
      console.error('获取题目详情失败:', error)
      throw error
    }
  },

  /**
   * 创建题目
   * @param {Object} questionData 题目数据
   * @returns {Promise} API响应
   */
  async createQuestion(questionData) {
    try {
      const response = await apiClient.post('/api/questions', questionData)
      return response.data
    } catch (error) {
    console.error('创建题目失败:', error)
    if (error.response && error.response.data) {
      console.error('后端错误详情:', error.response.data)
    }
    throw error
  }
  },

  /**
   * 更新题目
   * @param {number} id 题目ID
   * @param {Object} questionData 题目数据
   * @returns {Promise} API响应
   */
  async updateQuestion(id, questionData) {
    try {
      const response = await apiClient.put(`/api/questions/${id}`, questionData)
      return response.data
    } catch (error) {
      console.error('更新题目失败:', error)
      throw error
    }
  },

  /**
   * 删除题目
   * @param {number} id 题目ID
   * @returns {Promise} API响应
   */
  async deleteQuestion(id) {
    try {
      const response = await apiClient.delete(`/api/questions/${id}`)
      return response.data
    } catch (error) {
      console.error('删除题目失败:', error)
      throw error
    }
  },

  /**
   * 获取题目统计信息
   * @returns {Promise} API响应
   */
  async getQuestionStats() {
    try {
      const response = await apiClient.get('/api/questions/stats')
      return response.data
    } catch (error) {
      console.error('获取题目统计失败:', error)
      throw error
    }
  },

  /**
   * 健康检查
   * @returns {Promise} API响应
   */
  async healthCheck() {
    try {
      const response = await apiClient.get('/api/questions/health')
      return response.data
    } catch (error) {
      console.error('健康检查失败:', error)
      throw error
    }
  }
}

export default questionApi