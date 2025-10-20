// API配置文件
// 统一管理所有接口地址，方便部署时修改

// 开发环境配置
const DEV_CONFIG = {
  BASE_URL: 'http://localhost:8080',
  API_PREFIX: '/api'
}

// 生产环境配置
const PROD_CONFIG = {
  // 部署到服务器时，修改这里的地址
  BASE_URL: 'http://localhost:8080', // 本地地址
  API_PREFIX: '/api'
}

// 根据环境变量自动选择配置
const isProduction = process.env.NODE_ENV === 'production'
const config = isProduction ? PROD_CONFIG : DEV_CONFIG

// 导出配置
export const API_CONFIG = {
  BASE_URL: config.BASE_URL,
  API_PREFIX: config.API_PREFIX,
  
  // 完整的API基础地址
  get API_BASE_URL() {
    return `${this.BASE_URL}${this.API_PREFIX}`
  },
  
  // V1版本API地址
  get API_V1_URL() {
    return `${this.BASE_URL}${this.API_PREFIX}/v1`
  },
  
  // 认证相关API
  AUTH: {
    LOGIN: '/auth/login',
    STUDENT_LOGIN: '/auth/student/login',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh'
  },
  
  // 题目相关API
  QUESTIONS: {
    LIST: '/questions',
    CREATE: '/questions',
    UPDATE: (id) => `/questions/${id}`,
    DELETE: (id) => `/questions/${id}`,
    DETAIL: (id) => `/questions/${id}`,
    STATS: '/questions/stats',
    EXPORT: '/questions/export',
    IMPORT: '/questions/import',
    TEMPLATE: '/questions/template'
  },
  
  // 考试相关API
  EXAMS: {
    LIST: '/v1/exams',
    CREATE: '/v1/exams',
    UPDATE: (id) => `/v1/exams/${id}`,
    DELETE: (id) => `/v1/exams/${id}`,
    DETAIL: (id) => `/v1/exams/${id}`,
    RECORDS: '/v1/exams/records',
    RECORD_SCORE: (recordId) => `/v1/exams/records/${recordId}/score`,
    RECORD_RESET: (recordId) => `/v1/exams/records/${recordId}/reset`,
    RECORD_DELETE: (recordId) => `/v1/exams/records/${recordId}`
  },
  
  // 答案检查API
  ANSWER_CHECK: {
    SINGLE: '/v1/check-answer',
    BATCH: '/v1/check-answers'
  }
}

// 获取完整的API URL
export const getApiUrl = (endpoint) => {
  if (endpoint.startsWith('http')) {
    return endpoint
  }
  return `${API_CONFIG.BASE_URL}${endpoint}`
}

// 获取V1 API URL
export const getV1ApiUrl = (endpoint) => {
  if (endpoint.startsWith('http')) {
    return endpoint
  }
  return `${API_CONFIG.API_V1_URL}${endpoint}`
}

export default API_CONFIG