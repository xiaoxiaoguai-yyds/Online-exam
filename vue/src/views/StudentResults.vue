<template>
  <div class="student-results">
    <!-- 顶部导航栏 -->
    <header class="results-header">
      <div class="header-content">
        <div class="header-left">
          <button @click="goBack" class="back-btn">
            <svg viewBox="0 0 24 24" class="back-icon">
              <path d="M20,11V13H8L13.5,18.5L12.08,19.92L4.16,12L12.08,4.08L13.5,5.5L8,11H20Z"/>
            </svg>
            返回
          </button>
          <div class="page-title">
            <h1>我的考试成绩</h1>
            <p>查看所有考试记录和成绩详情</p>
          </div>
        </div>
        <div class="header-right">
          <div class="student-info">
            <span class="student-name">{{ studentInfo.name }}</span>
            <span class="student-number">{{ studentInfo.studentNumber }}</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="results-main">
      <div class="results-container">
        <!-- 成绩统计卡片 -->
        <div class="stats-section">
          <div class="stats-grid">
            <div class="stat-card total">
              <div class="stat-icon">📊</div>
              <div class="stat-content">
                <div class="stat-number">{{ examStats.total }}</div>
                <div class="stat-label">总考试次数</div>
              </div>
            </div>
            <div class="stat-card passed">
              <div class="stat-icon">✅</div>
              <div class="stat-content">
                <div class="stat-number">{{ examStats.passed }}</div>
                <div class="stat-label">通过次数</div>
              </div>
            </div>
            <div class="stat-card average">
              <div class="stat-icon">📈</div>
              <div class="stat-content">
                <div class="stat-number">{{ examStats.average }}%</div>
                <div class="stat-label">平均分数</div>
              </div>
            </div>
            <div class="stat-card rate">
              <div class="stat-icon">🎯</div>
              <div class="stat-content">
                <div class="stat-number">{{ examStats.passRate }}%</div>
                <div class="stat-label">通过率</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 筛选和搜索区域 -->
        <div class="filter-section">
          <div class="filter-row">
            <div class="filter-group">
              <label>成绩筛选:</label>
              <select v-model="scoreFilter" @change="filterResults">
                <option value="">全部成绩</option>
                <option value="excellent">优秀 (90-100)</option>
                <option value="good">良好 (80-89)</option>
                <option value="pass">及格 (60-79)</option>
                <option value="fail">不及格 (0-59)</option>
              </select>
            </div>
            <div class="filter-group">
              <label>时间排序:</label>
              <select v-model="sortOrder" @change="sortResults">
                <option value="desc">最新优先</option>
                <option value="asc">最早优先</option>
              </select>
            </div>
            <div class="search-group">
              <input 
                type="text" 
                v-model="searchKeyword" 
                @input="filterResults"
                placeholder="搜索考试名称..."
                class="search-input"
              >
              <svg viewBox="0 0 24 24" class="search-icon">
                <path d="M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- 考试成绩列表 -->
        <div class="results-section">
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载考试成绩...</p>
          </div>

          <div v-else-if="filteredResults.length === 0" class="empty-state">
            <svg viewBox="0 0 24 24" class="empty-icon">
              <path d="M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4M11,16.5L6.5,12L7.91,10.59L11,13.67L16.59,8.09L18,9.5L11,16.5Z"/>
            </svg>
            <h3>暂无考试记录</h3>
            <p>{{ searchKeyword ? '没有找到匹配的考试记录' : '您还没有参加任何考试' }}</p>
          </div>

          <div v-else class="results-list">
            <div v-for="result in filteredResults" :key="result.id" class="result-card" :class="getScoreClass(result.score)">
              <div class="result-header">
                <div class="exam-info">
                  <h3 class="exam-title">{{ result.examTitle }}</h3>
                  <div class="exam-meta">
                    <span class="exam-date">
                      <svg viewBox="0 0 24 24" class="meta-icon">
                        <path d="M19,3H18V1H16V3H8V1H6V3H5A2,2 0 0,0 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5A2,2 0 0,0 19,3M19,19H5V8H19V19Z"/>
                      </svg>
                      {{ formatDate(result.submitTime) }}
                    </span>
                    <span class="exam-duration">
                      <svg viewBox="0 0 24 24" class="meta-icon">
                        <path d="M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4M12.5,7V12.25L17,14.92L16.25,16.15L11,13V7H12.5Z"/>
                      </svg>
                      用时 {{ result.duration || '未知' }}
                    </span>
                  </div>
                </div>
                <div class="score-display">
                  <div class="score-number">{{ Math.round(result.score) }}</div>
                  <div class="score-total">/ {{ result.totalScore || 100 }}</div>
                  <div class="score-badge" :class="getScoreClass(result.score)">
                    {{ getScoreLabel(result.score) }}
                  </div>
                </div>
              </div>
              
              <div class="result-details">
                <div class="detail-row">
                  <div class="detail-item">
                    <span class="detail-label">正确题数:</span>
                    <span class="detail-value">{{ result.correctCount || 0 }} / {{ result.totalQuestions || 0 }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">正确率:</span>
                    <span class="detail-value">{{ getAccuracyRate(result) }}%</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">提交状态:</span>
                    <span class="detail-value" :class="getStatusClass(result.status)">
                      {{ getStatusText(result.status) }}
                    </span>
                  </div>
                </div>
              </div>

              <div class="result-actions">
                <button @click="viewDetails(result)" class="action-btn primary">
                  <svg viewBox="0 0 24 24" class="btn-icon">
                    <path d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17,19.5 21.27,16.39 23,12C21.27,7.61 17,4.5 12,4.5Z"/>
                  </svg>
                  查看详情
                </button>
                <button v-if="result.status === 2" @click="reviewAnswers(result)" class="action-btn secondary">
                  <svg viewBox="0 0 24 24" class="btn-icon">
                    <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                  </svg>
                  查看答案
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="detail-modal" @click.stop>
        <div class="modal-header">
          <h3>考试详情</h3>
          <button @click="closeDetailModal" class="close-btn">
            <svg viewBox="0 0 24 24">
              <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
            </svg>
          </button>
        </div>
        <div class="modal-content" v-if="selectedResult">
          <div class="detail-section">
            <h4>基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">考试名称:</span>
                <span class="info-value">{{ selectedResult.examTitle }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">考试时间:</span>
                <span class="info-value">{{ formatDate(selectedResult.submitTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">考试分数:</span>
                <span class="info-value">{{ Math.round(selectedResult.score) }} / {{ selectedResult.totalScore || 100 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">用时:</span>
                <span class="info-value">{{ selectedResult.duration || '未知' }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h4>成绩分析</h4>
            <div class="score-analysis">
              <div class="analysis-item">
                <div class="analysis-label">总题数</div>
                <div class="analysis-value">{{ selectedResult.totalQuestions || 0 }}</div>
              </div>
              <div class="analysis-item">
                <div class="analysis-label">正确题数</div>
                <div class="analysis-value">{{ selectedResult.correctCount || 0 }}</div>
              </div>
              <div class="analysis-item">
                <div class="analysis-label">错误题数</div>
                <div class="analysis-value">{{ (selectedResult.totalQuestions || 0) - (selectedResult.correctCount || 0) }}</div>
              </div>
              <div class="analysis-item">
                <div class="analysis-label">正确率</div>
                <div class="analysis-value">{{ getAccuracyRate(selectedResult) }}%</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 答案详情弹窗 -->
    <div v-if="showAnswerModal" class="modal-overlay" @click="closeAnswerModal">
      <div class="answer-modal" @click.stop>
        <div class="modal-header">
          <h3>考试答案详情</h3>
          <button @click="closeAnswerModal" class="close-btn">
            <svg viewBox="0 0 24 24">
              <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
            </svg>
          </button>
        </div>
        <div class="modal-content" v-if="selectedAnswers">
          <div class="answer-header">
            <h4>{{ selectedResult?.examTitle }}</h4>
            <div class="answer-summary">
              <span class="summary-item">
                <strong>总分:</strong> {{ Math.round(selectedResult?.score || 0) }} / {{ selectedResult?.totalScore || 100 }}
              </span>
              <span class="summary-item">
                <strong>正确率:</strong> {{ getAccuracyRate(selectedResult) }}%
              </span>
            </div>
          </div>
          
          <div class="answers-list">
            <div v-for="(answer, index) in selectedAnswers" :key="answer.id" class="answer-item">
              <div class="question-header">
                <div class="question-number">第 {{ index + 1 }} 题</div>
                <div class="question-type">{{ getQuestionTypeText(answer.question?.type) }}</div>
                <div class="answer-status" :class="answer.isCorrect ? 'correct' : 'incorrect'">
                  {{ answer.isCorrect ? '正确' : '错误' }}
                </div>
              </div>
              
              <div class="question-content">
                <div class="question-text">
                  <strong>题目:</strong> {{ answer.question?.content || '题目内容加载失败' }}
                </div>
                
                <div v-if="answer.question?.options" class="question-options">
                  <div v-for="(option, optionIndex) in getQuestionOptions(answer.question.options)" 
                       :key="optionIndex" 
                       class="option-item"
                       :class="{ 
                         'selected': isOptionSelected(answer.studentAnswer, optionIndex),
                         'correct': isCorrectOption(answer.correctAnswer, optionIndex)
                       }">
                    <span class="option-label">{{ String.fromCharCode(65 + optionIndex) }}.</span>
                    <span class="option-text">{{ option }}</span>
                    <span v-if="isOptionSelected(answer.studentAnswer, optionIndex)" class="option-mark student">你的答案</span>
                    <span v-if="isCorrectOption(answer.correctAnswer, optionIndex)" class="option-mark correct">正确答案</span>
                  </div>
                </div>
                
                <div class="answer-details">
                  <div class="student-answer">
                    <strong>你的答案:</strong> 
                    <span :class="answer.isCorrect ? 'correct-text' : 'incorrect-text'">
                      {{ formatStudentAnswer(answer.studentAnswer, answer.question?.type, getQuestionOptions(answer.question?.options)) }}
                    </span>
                  </div>
                  <div class="correct-answer">
                    <strong>正确答案:</strong> 
                    <span class="correct-text">
                      {{ formatCorrectAnswer(answer.correctAnswer, answer.question?.type, getQuestionOptions(answer.question?.options)) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="loading-answers">
          <div class="loading-spinner"></div>
          <p>正在加载答案详情...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StudentResults',
  data() {
    return {
      studentInfo: {
        name: '',
        studentNumber: '',
        id: null
      },
      examResults: [],
      filteredResults: [],
      loading: true,
      searchKeyword: '',
      scoreFilter: '',
      sortOrder: 'desc',
      showDetailModal: false,
      selectedResult: null,
      showAnswerModal: false,
      selectedAnswers: null,
      loadingAnswers: false,
      examStats: {
        total: 0,
        passed: 0,
        average: 0,
        passRate: 0
      }
    }
  },
  async mounted() {
    this.loadStudentInfo()
    await this.loadExamResults()
    this.calculateStats()
    this.filterResults()
  },
  methods: {
    loadStudentInfo() {
      const studentInfo = localStorage.getItem('studentInfo')
      if (studentInfo) {
        this.studentInfo = JSON.parse(studentInfo)
      }
    },
    
    async loadExamResults() {
      try {
        this.loading = true
        
        if (!this.studentInfo.id) {
          console.error('未找到学生ID')
          alert('请重新登录')
          this.$router.push('/student/login')
          return
        }

        // 调用API获取学生所有考试记录
        const response = await axios.get('/api/v1/student/exam-records/recent', {
          params: {
            studentId: this.studentInfo.id,
            limit: 100 // 获取所有记录
          }
        })
        
        if (response.data && response.data.code === 200) {
          this.examResults = (response.data.data || []).map(record => ({
            id: record.id,
            examTitle: record.exam ? record.exam.title : '未知考试',
            score: record.score || 0,
            totalScore: record.exam ? record.exam.totalScore : 100,
            submitTime: record.submitTime,
            duration: this.calculateDuration(record.startTime, record.submitTime),
            status: record.status,
            correctCount: record.correctCount || 0,
            totalQuestions: record.exam ? record.exam.questionCount : 0,
            startTime: record.startTime
          }))
          console.log('成功加载考试成绩:', this.examResults)
        } else {
          console.error('API返回错误:', response.data)
          alert(response.data.message || '获取考试成绩失败')
        }
      } catch (error) {
        console.error('加载考试成绩失败:', error)
        if (error.response) {
          alert(error.response.data.message || '获取考试成绩失败')
        } else {
          alert('网络错误，请检查连接')
        }
      } finally {
        this.loading = false
      }
    },
    
    calculateStats() {
      const total = this.examResults.length
      const passed = this.examResults.filter(result => result.score >= 60).length
      const totalScore = this.examResults.reduce((sum, result) => sum + result.score, 0)
      const average = total > 0 ? Math.round(totalScore / total) : 0
      const passRate = total > 0 ? Math.round((passed / total) * 100) : 0
      
      this.examStats = {
        total,
        passed,
        average,
        passRate
      }
    },
    
    filterResults() {
      let filtered = [...this.examResults]
      
      // 按关键词搜索
      if (this.searchKeyword) {
        filtered = filtered.filter(result => 
          result.examTitle.toLowerCase().includes(this.searchKeyword.toLowerCase())
        )
      }
      
      // 按成绩筛选
      if (this.scoreFilter) {
        filtered = filtered.filter(result => {
          const score = result.score
          switch (this.scoreFilter) {
            case 'excellent': return score >= 90
            case 'good': return score >= 80 && score < 90
            case 'pass': return score >= 60 && score < 80
            case 'fail': return score < 60
            default: return true
          }
        })
      }
      
      this.filteredResults = filtered
      this.sortResults()
    },
    
    sortResults() {
      this.filteredResults.sort((a, b) => {
        const dateA = new Date(a.submitTime)
        const dateB = new Date(b.submitTime)
        return this.sortOrder === 'desc' ? dateB - dateA : dateA - dateB
      })
    },
    
    calculateDuration(startTime, endTime) {
      if (!startTime || !endTime) return '未知'
      const start = new Date(startTime)
      const end = new Date(endTime)
      const diffMs = end - start
      const diffMins = Math.round(diffMs / (1000 * 60))
      return `${diffMins}分钟`
    },
    
    formatDate(dateString) {
      if (!dateString) return '未知时间'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    getScoreClass(score) {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 60) return 'pass'
      return 'fail'
    },
    
    getScoreLabel(score) {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 60) return '及格'
      return '不及格'
    },
    
    getStatusClass(status) {
      switch (status) {
        case 0: return 'not-started'
        case 1: return 'in-progress'
        case 2: return 'submitted'
        case 3: return 'timeout'
        default: return 'unknown'
      }
    },
    
    getStatusText(status) {
      switch (status) {
        case 0: return '未开始'
        case 1: return '进行中'
        case 2: return '已提交'
        case 3: return '超时提交'
        default: return '未知'
      }
    },
    
    getAccuracyRate(result) {
      if (!result.totalQuestions || result.totalQuestions === 0) return 0
      return Math.round((result.correctCount / result.totalQuestions) * 100)
    },
    
    viewDetails(result) {
      this.selectedResult = result
      this.showDetailModal = true
    },
    
    closeDetailModal() {
      this.showDetailModal = false
      this.selectedResult = null
    },
    
    async reviewAnswers(result) {
      this.selectedResult = result
      this.showAnswerModal = true
      this.selectedAnswers = null
      this.loadingAnswers = true
      
      try {
        const response = await axios.get(`/api/v1/student/exam-records/${result.id}/answers`, {
          params: {
            studentId: this.studentInfo.id
          },
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        })
        
        if (response.data && response.data.code === 200) {
          this.selectedAnswers = response.data.data
        } else {
          this.$message.error(response.data.message || '获取答案详情失败')
        }
      } catch (error) {
        console.error('获取答案详情失败:', error)
        this.$message.error('获取答案详情失败，请稍后重试')
      } finally {
        this.loadingAnswers = false
      }
    },
    
    closeAnswerModal() {
      this.showAnswerModal = false
      this.selectedAnswers = null
      this.selectedResult = null
    },
    
    getQuestionTypeText(type) {
      const typeMap = {
        'single': '单选题',
        'multiple': '多选题',
        'judge': '判断题',
        'fill': '填空题',
        'essay': '简答题'
      }
      return typeMap[type] || '未知题型'
    },
    
    getQuestionOptions(options) {
      if (typeof options === 'string') {
        try {
          return JSON.parse(options)
        } catch (e) {
          return options.split('\n').filter(opt => opt.trim())
        }
      }
      return Array.isArray(options) ? options : []
    },
    
    isOptionSelected(studentAnswer, optionIndex) {
      if (!studentAnswer) return false
      const answer = studentAnswer.toString().toUpperCase()
      const optionLetter = String.fromCharCode(65 + optionIndex)
      return answer.includes(optionLetter)
    },
    
    isCorrectOption(correctAnswer, optionIndex) {
      if (!correctAnswer) return false
      const answer = correctAnswer.toString().toUpperCase()
      const optionLetter = String.fromCharCode(65 + optionIndex)
      return answer.includes(optionLetter)
    },
    
    formatStudentAnswer(answer, questionType, options) {
      if (!answer) return '未作答'
      
      switch (questionType) {
        case 'judge':
          return answer === 'true' || answer === '1' ? '正确' : '错误'
        case 'single':
          // 将索引转换为选项文本
          const answerIndex = parseInt(answer)
          if (!isNaN(answerIndex) && options && options[answerIndex]) {
            return `${String.fromCharCode(65 + answerIndex)}. ${options[answerIndex]}`
          }
          return answer.toString()
        case 'multiple':
          // 处理多选答案
          if (typeof answer === 'string') {
            try {
              const answerArray = JSON.parse(answer)
              if (Array.isArray(answerArray) && options) {
                return answerArray.map(idx => {
                  const index = parseInt(idx)
                  if (options[index]) {
                    return `${String.fromCharCode(65 + index)}. ${options[index]}`
                  }
                  return idx
                }).join(', ')
              }
            } catch (e) {
              // 如果不是JSON格式，按逗号分割
              const answerArray = answer.split(',').map(a => a.trim())
              if (options) {
                return answerArray.map(idx => {
                  const index = parseInt(idx)
                  if (options[index]) {
                    return `${String.fromCharCode(65 + index)}. ${options[index]}`
                  }
                  return idx
                }).join(', ')
              }
            }
          }
          return answer.toString().toUpperCase()
        default:
          return answer.toString()
      }
    },
    
    formatCorrectAnswer(answer, questionType, options) {
      if (!answer) return '无'
      
      switch (questionType) {
        case 'judge':
          return answer === 'true' || answer === '1' ? '正确' : '错误'
        case 'single':
          // 处理单选正确答案
          if (typeof answer === 'string') {
            try {
              const answerArray = JSON.parse(answer)
              if (Array.isArray(answerArray) && answerArray.length > 0) {
                const index = parseInt(answerArray[0])
                if (options && options[index]) {
                  return `${String.fromCharCode(65 + index)}. ${options[index]}`
                }
                return String.fromCharCode(65 + index)
              }
            } catch (e) {
              // 如果不是JSON格式，直接处理
              const index = parseInt(answer)
              if (!isNaN(index) && options && options[index]) {
                return `${String.fromCharCode(65 + index)}. ${options[index]}`
              }
            }
          }
          return answer.toString()
        case 'multiple':
          // 处理多选正确答案
          if (typeof answer === 'string') {
            try {
              const answerArray = JSON.parse(answer)
              if (Array.isArray(answerArray) && options) {
                return answerArray.map(idx => {
                  const index = parseInt(idx)
                  if (options[index]) {
                    return `${String.fromCharCode(65 + index)}. ${options[index]}`
                  }
                  return idx
                }).join(', ')
              }
            } catch (e) {
              // 如果不是JSON格式，按逗号分割
              const answerArray = answer.split(',').map(a => a.trim())
              if (options) {
                return answerArray.map(idx => {
                  const index = parseInt(idx)
                  if (options[index]) {
                    return `${String.fromCharCode(65 + index)}. ${options[index]}`
                  }
                  return idx
                }).join(', ')
              }
            }
          }
          return answer.toString().toUpperCase()
        default:
          return answer.toString()
      }
    },
    
    goBack() {
      this.$router.push('/student/dashboard')
    }
  }
}
</script>

<style scoped>
.student-results {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 顶部导航栏 */
.results-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1rem 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  color: #495057;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.back-btn:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.back-icon {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

.page-title h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.page-title p {
  margin: 0.25rem 0 0 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.header-right .student-info {
  text-align: right;
}

.student-name {
  display: block;
  font-weight: 600;
  color: #2c3e50;
}

.student-number {
  display: block;
  font-size: 0.9rem;
  color: #6c757d;
}

/* 主要内容区域 */
.results-main {
  padding: 2rem 0;
}

.results-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.stat-card.total .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-card.passed .stat-icon { background: linear-gradient(135deg, #56ab2f, #a8e6cf); }
.stat-card.average .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-card.rate .stat-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
  margin-top: 0.25rem;
}

/* 筛选区域 */
.filter-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 500;
  color: #495057;
  white-space: nowrap;
}

.filter-group select {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  background: white;
  color: #495057;
  min-width: 120px;
}

.search-group {
  position: relative;
  margin-left: auto;
}

.search-input {
  padding: 0.5rem 1rem 0.5rem 2.5rem;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  background: white;
  color: #495057;
  min-width: 200px;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  fill: #6c757d;
}

/* 结果列表 */
.results-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-icon {
  width: 64px;
  height: 64px;
  fill: #dee2e6;
  margin: 0 auto 1rem;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.result-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  border-left: 4px solid #dee2e6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.result-card.excellent { border-left-color: #28a745; }
.result-card.good { border-left-color: #17a2b8; }
.result-card.pass { border-left-color: #ffc107; }
.result-card.fail { border-left-color: #dc3545; }

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.exam-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.exam-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.exam-meta span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.meta-icon {
  width: 14px;
  height: 14px;
  fill: currentColor;
}

.score-display {
  text-align: right;
}

.score-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

.score-total {
  font-size: 1rem;
  color: #6c757d;
  margin-left: 0.25rem;
}

.score-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  margin-top: 0.5rem;
}

.score-badge.excellent { background: #d4edda; color: #155724; }
.score-badge.good { background: #d1ecf1; color: #0c5460; }
.score-badge.pass { background: #fff3cd; color: #856404; }
.score-badge.fail { background: #f8d7da; color: #721c24; }

.result-details {
  margin-bottom: 1rem;
}

.detail-row {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  gap: 0.5rem;
}

.detail-label {
  color: #6c757d;
  font-size: 0.9rem;
}

.detail-value {
  color: #495057;
  font-weight: 500;
  font-size: 0.9rem;
}

.detail-value.submitted { color: #28a745; }
.detail-value.timeout { color: #dc3545; }

.result-actions {
  display: flex;
  gap: 0.75rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: #667eea;
  color: white;
}

.action-btn.primary:hover {
  background: #5a6fd8;
}

.action-btn.secondary {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
}

.action-btn.secondary:hover {
  background: #e9ecef;
}

.btn-icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
}

/* 详情弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 2rem;
}

.detail-modal {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  padding: 0.5rem;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.3s ease;
}

.close-btn:hover {
  background: #f8f9fa;
}

.close-btn svg {
  width: 20px;
  height: 20px;
  fill: #6c757d;
}

.modal-content {
  padding: 1.5rem;
}

.detail-section {
  margin-bottom: 2rem;
}

.detail-section h4 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-label {
  font-size: 0.9rem;
  color: #6c757d;
}

.info-value {
  font-weight: 500;
  color: #495057;
}

.score-analysis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.analysis-item {
  text-align: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.analysis-label {
  font-size: 0.9rem;
  color: #6c757d;
  margin-bottom: 0.5rem;
}

.analysis-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2c3e50;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 1rem;
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .results-container {
    padding: 0 1rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .search-group {
    margin-left: 0;
  }

  .result-header {
    flex-direction: column;
    gap: 1rem;
  }

  .score-display {
    text-align: left;
  }

  .detail-row {
    flex-direction: column;
    gap: 0.5rem;
  }

  .modal-overlay {
    padding: 1rem;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .score-analysis {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 答案详情弹窗样式 */
.answer-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 900px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.answer-modal .modal-content {
  flex: 1;
  overflow-y: auto;
  padding: 0 1.5rem 1.5rem;
}

.answer-header {
  padding: 1rem 0;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 1.5rem;
}

.answer-header h4 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.answer-summary {
  display: flex;
  gap: 2rem;
  color: #6c757d;
  font-size: 0.9rem;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.answers-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.answer-item {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.question-header {
  background: #f8f9fa;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e9ecef;
}

.question-number {
  font-weight: bold;
  color: #495057;
}

.question-type {
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #6c757d;
}

.answer-status {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.answer-status.correct {
  background: #d4edda;
  color: #155724;
}

.answer-status.incorrect {
  background: #f8d7da;
  color: #721c24;
}

.question-content {
  padding: 1.5rem;
}

.question-text {
  margin-bottom: 1rem;
  line-height: 1.6;
}

.question-options {
  margin: 1rem 0;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 0.75rem;
  margin: 0.5rem 0;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  position: relative;
  transition: all 0.3s ease;
}

.option-item.selected {
  border-color: #007bff;
  background: #f0f8ff;
}

.option-item.correct {
  border-color: #28a745;
  background: #f8fff9;
}

.option-item.selected.correct {
  border-color: #28a745;
  background: #d4edda;
}

.option-label {
  font-weight: bold;
  margin-right: 0.5rem;
  min-width: 1.5rem;
}

.option-text {
  flex: 1;
}

.option-mark {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-weight: bold;
}

.option-mark.student {
  background: #cce5ff;
  color: #0056b3;
}

.option-mark.correct {
  background: #d4edda;
  color: #155724;
}

.answer-details {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.student-answer, .correct-answer {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.correct-text {
  color: #28a745;
  font-weight: bold;
}

.incorrect-text {
  color: #dc3545;
  font-weight: bold;
}

.loading-answers {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: #6c757d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .answer-modal {
    width: 95%;
    max-height: 95vh;
  }
  
  .answer-summary {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .question-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .answer-details {
    grid-template-columns: 1fr;
  }
  
  .option-mark {
    position: static;
    transform: none;
    margin-left: auto;
  }
}
</style>