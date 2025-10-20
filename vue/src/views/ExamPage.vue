<template>
  <div class="exam-container">
    <!-- 考试头部信息 -->
    <div class="exam-header">
      <div class="exam-info">
        <h1 class="exam-title">{{ examInfo.title }}</h1>
        <p class="exam-description">{{ examInfo.description }}</p>
        <div class="exam-meta">
          <span class="exam-duration">
            <i class="icon-clock"></i>
            考试时长：{{ examInfo.duration }}分钟
          </span>
          <span class="exam-questions">
            <i class="icon-list"></i>
            题目数量：{{ questions.length }}题
          </span>
          <span class="exam-score">
            <i class="icon-star"></i>
            总分：{{ examInfo.totalScore }}分
          </span>
        </div>
      </div>
      
      <!-- 考试计时器 -->
      <div class="exam-timer">
        <div class="timer-display">
          <span class="time-label">剩余时间</span>
          <div class="time-value" :class="{ 'time-warning': timeWarning, 'time-danger': timeDanger }">
            {{ formatTime(remainingTime) }}
          </div>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- 题目导航 -->
    <div class="question-nav">
      <div class="nav-title">题目导航</div>
      <div class="nav-grid">
        <button 
          v-for="(question, index) in questions" 
          :key="question.id"
          class="nav-item"
          :class="{
            'current': currentQuestionIndex === index,
            'answered': isQuestionAnswered(index),
            'flagged': flaggedQuestions.includes(index)
          }"
          @click="goToQuestion(index)"
        >
          {{ index + 1 }}
        </button>
      </div>
      <div class="nav-legend">
        <span class="legend-item">
          <span class="legend-dot current"></span>
          当前题目
        </span>
        <span class="legend-item">
          <span class="legend-dot answered"></span>
          已答题
        </span>
        <span class="legend-item">
          <span class="legend-dot flagged"></span>
          已标记
        </span>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="exam-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-area">
        <div class="loading-spinner"></div>
        <p>正在加载考试题目...</p>
      </div>
      
      <!-- 无题目状态 -->
      <div v-else-if="!currentQuestion && questions.length === 0" class="empty-area">
        <div class="empty-icon">📝</div>
        <p>暂无考试题目</p>
        <button class="retry-btn" @click="reloadExam">重新加载</button>
      </div>
      
      <!-- 题目区域 -->
      <div class="question-area" v-else-if="currentQuestion">
        <div class="question-header">
          <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
          <span class="question-type">{{ getQuestionTypeText(currentQuestion.type) }}</span>
          <span class="question-score">({{ currentQuestion.score }}分)</span>
          <button 
            class="flag-button"
            :class="{ 'flagged': flaggedQuestions.includes(currentQuestionIndex) }"
            @click="toggleFlag(currentQuestionIndex)"
          >
            <i class="icon-flag"></i>
            {{ flaggedQuestions.includes(currentQuestionIndex) ? '取消标记' : '标记题目' }}
          </button>
        </div>

        <div class="question-content">
          <div class="question-text" v-html="currentQuestion.content"></div>
          
          <!-- 单选题 -->
          <div v-if="currentQuestion.type === 'single'" class="answer-options">
            <div 
              v-for="(option, optionIndex) in currentQuestion.options" 
              :key="optionIndex"
              class="option-item"
              @click="selectSingleChoice(optionIndex)"
            >
              <input 
                type="radio" 
                :name="'question_' + currentQuestion.questionId"
                :value="optionIndex"
                v-model="answers[currentQuestion.questionId]"
                :id="'option_' + currentQuestion.questionId + '_' + optionIndex"
              >
              <label :for="'option_' + currentQuestion.questionId + '_' + optionIndex" class="option-label">
                <span class="option-letter">{{ String.fromCharCode(65 + optionIndex) }}</span>
                <span class="option-text">{{ option }}</span>
              </label>
            </div>
          </div>

          <!-- 多选题 -->
          <div v-else-if="currentQuestion.type === 'multiple'" class="answer-options">
            <div 
              v-for="(option, optionIndex) in currentQuestion.options" 
              :key="optionIndex"
              class="option-item"
            >
              <input 
                type="checkbox" 
                :value="optionIndex"
                v-model="answers[currentQuestion.questionId]"
                :id="'option_' + currentQuestion.questionId + '_' + optionIndex"
              >
              <label :for="'option_' + currentQuestion.questionId + '_' + optionIndex" class="option-label">
                <span class="option-letter">{{ String.fromCharCode(65 + optionIndex) }}</span>
                <span class="option-text">{{ option }}</span>
              </label>
            </div>
          </div>

          <!-- 判断题 -->
          <div v-else-if="currentQuestion.type === 'judge'" class="answer-options">
            <div class="option-item" @click="selectTrueFalse(true)">
              <input 
                type="radio" 
                :name="'question_' + currentQuestion.questionId"
                value="true"
                v-model="answers[currentQuestion.questionId]"
                :id="'true_' + currentQuestion.questionId"
              >
              <label :for="'true_' + currentQuestion.questionId" class="option-label">
                <span class="option-letter">√</span>
                <span class="option-text">正确</span>
              </label>
            </div>
            <div class="option-item" @click="selectTrueFalse(false)">
              <input 
                type="radio" 
                :name="'question_' + currentQuestion.questionId"
                value="false"
                v-model="answers[currentQuestion.questionId]"
                :id="'false_' + currentQuestion.questionId"
              >
              <label :for="'false_' + currentQuestion.questionId" class="option-label">
                <span class="option-letter">×</span>
                <span class="option-text">错误</span>
              </label>
            </div>
          </div>

          <!-- 填空题 -->
          <div v-else-if="currentQuestion.type === 'fill'" class="answer-input">
            <textarea 
              v-model="answers[currentQuestion.questionId]"
              class="fill-input"
              placeholder="请输入答案..."
              rows="4"
            ></textarea>
          </div>

          <!-- 简答题 -->
          <div v-else-if="currentQuestion.type === 'essay'" class="answer-input">
            <textarea 
              v-model="answers[currentQuestion.questionId]"
              class="short-answer-input"
              placeholder="请输入答案..."
              rows="6"
            ></textarea>
          </div>
        </div>

        <!-- 题目导航按钮 -->
        <div class="question-navigation">
          <button 
            class="nav-btn prev-btn"
            :disabled="currentQuestionIndex === 0"
            @click="previousQuestion"
          >
            <i class="icon-arrow-left"></i>
            上一题
          </button>
          
          <button 
            class="nav-btn next-btn"
            :disabled="currentQuestionIndex === questions.length - 1"
            @click="nextQuestion"
          >
            下一题
            <i class="icon-arrow-right"></i>
          </button>
        </div>
      </div>

      <!-- 考试操作区域 -->
      <div class="exam-actions">
        <div class="action-stats">
          <div class="stat-item">
            <span class="stat-label">已答题</span>
            <span class="stat-value">{{ answeredCount }}/{{ questions.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已标记</span>
            <span class="stat-value">{{ flaggedQuestions.length }}</span>
          </div>
        </div>
        
        <div class="action-buttons">
          <button class="action-btn save-btn" @click="saveAnswers">
            <i class="icon-save"></i>
            保存答案
          </button>
          <button class="action-btn submit-btn" @click="showSubmitConfirm = true">
            <i class="icon-check"></i>
            提交考试
          </button>
        </div>
      </div>
    </div>

    <!-- 提交确认对话框 -->
    <div v-if="showSubmitConfirm" class="modal-overlay" @click="showSubmitConfirm = false">
      <div class="submit-modal" @click.stop>
        <div class="modal-header">
          <h3>确认提交考试</h3>
        </div>
        <div class="modal-content">
          <div class="submit-stats">
            <p>您已完成 <strong>{{ answeredCount }}</strong> 题，还有 <strong>{{ questions.length - answeredCount }}</strong> 题未答</p>
            <p v-if="flaggedQuestions.length > 0">您标记了 <strong>{{ flaggedQuestions.length }}</strong> 题需要检查</p>
            <p class="warning-text">提交后将无法修改答案，请确认是否提交？</p>
          </div>
        </div>
        <div class="modal-actions">
          <button class="modal-btn cancel-btn" @click="showSubmitConfirm = false">
            取消
          </button>
          <button class="modal-btn confirm-btn" @click="submitExam">
            确认提交
          </button>
        </div>
      </div>
    </div>

    <!-- 右上角通知提示 -->
    <div v-if="showNotification" class="notification-container">
      <div class="notification-item" :class="notificationType">
        <div class="notification-content">
          <i class="notification-icon" :class="getNotificationIcon()"></i>
          <span class="notification-message">{{ notificationMessage }}</span>
        </div>
        <button class="notification-close" @click="closeNotification">×</button>
      </div>
    </div>

    <!-- 全屏提示对话框 -->
    <div v-if="showFullscreenDialog" class="modal-overlay">
      <div class="fullscreen-modal" @click.stop>
        <div class="modal-header">
          <h3>进入考试全屏模式</h3>
        </div>
        <div class="modal-content">
          <p>为了确保考试的公平性和安全性，需要进入全屏模式。</p>
          <p>请点击下方按钮进入全屏模式开始考试。</p>
        </div>
        <div class="modal-actions">
          <button class="modal-btn confirm-btn" @click="userEnterFullscreen">
            进入全屏开始考试
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>正在加载考试...</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ExamPage',
  data() {
    return {
      loading: true,
      examInfo: {
        id: null,
        title: '',
        description: '',
        duration: 0,
        totalScore: 0,
        startTime: null,
        endTime: null
      },
      questions: [],
      answers: {},
      currentQuestionIndex: 0,
      flaggedQuestions: [],
      remainingTime: 0,
      timer: null,
      showSubmitConfirm: false,
      examStartTime: null,
       hasUserGesture: false,
       isFullscreenActive: false,
       showFullscreenDialog: false,
       focusViolationCount: 0,
       focusLossCount: 0,
       showNotification: false,
       notificationMessage: '',
       notificationType: 'warning',
       retryCount: 0,
       fullscreenRetryCount: 0,
       recoveryDelay: 2000, // 焦点恢复延迟时间
       focusLossHistory: [], // 失焦历史记录
       isRecoveringFocus: false, // 是否正在恢复焦点
       fullscreenRecoveryTimer: null, // 全屏恢复定时器
       gestureDetectionTimeout: null, // 手势检测超时定时器
       // 全屏锁定模式相关变量
       isFullscreenLockMode: false, // 是否启用全屏锁定模式
       focusLockInterval: null, // 焦点锁定定时器
       keepOnTopInterval: null, // 窗口置顶定时器
       windowBlurHandler: null, // 窗口失焦处理器
       visibilityLockHandler: null // 页面可见性锁定处理器
    }
  },
  computed: {
    currentQuestion() {
      if (!this.questions || this.questions.length === 0) {
        return null
      }
      
      const examQuestion = this.questions[this.currentQuestionIndex]
      console.log('当前examQuestion结构:', examQuestion)
      
      if (examQuestion) {
        // 处理选项数据
        let options = []
        if (examQuestion.questionOptions) {
          try {
            // 尝试解析JSON格式的选项
            options = JSON.parse(examQuestion.questionOptions)
          } catch (e) {
            // 如果不是JSON，按逗号分割
            options = examQuestion.questionOptions.split(',')
          }
        }
        
        return {
          id: examQuestion.id,
          examId: examQuestion.examId,
          questionId: examQuestion.questionId,
          title: examQuestion.questionTitle,
          content: examQuestion.questionContent,
          type: examQuestion.questionType?.toLowerCase() || 'single',
          difficulty: examQuestion.questionDifficulty?.toLowerCase() || 'easy',
          options: options,
          correctAnswer: examQuestion.questionCorrectAnswer,
          correctAnswers: examQuestion.questionCorrectAnswers,
          tags: examQuestion.questionTags,
          score: examQuestion.score || 0,
          questionOrder: examQuestion.questionOrder
        }
      }
      
      return null
    },
    answeredCount() {
      return Object.keys(this.answers).filter(questionId => {
        const answer = this.answers[questionId]
        if (Array.isArray(answer)) {
          return answer.length > 0
        }
        return answer !== null && answer !== undefined && answer !== ''
      }).length
    },
    progressPercentage() {
      if (this.examInfo.duration === 0) return 0
      const totalTime = this.examInfo.duration * 60 // 转换为秒
      const elapsed = totalTime - this.remainingTime
      return Math.min((elapsed / totalTime) * 100, 100)
    },
    timeWarning() {
      const totalTime = this.examInfo.duration * 60
      return this.remainingTime <= totalTime * 0.25 && this.remainingTime > totalTime * 0.1
    },
    timeDanger() {
      const totalTime = this.examInfo.duration * 60
      return this.remainingTime <= totalTime * 0.1
    }
  },
  async mounted() {
    await this.loadExam()
    this.initializeAnswers()
    this.startTimer()
    // 启用考试安全模式
    this.enableExamSecurityMode()
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    // 停止全屏恢复机制
    this.stopFullscreenRecovery()
    // 移除用户手势检测
    this.removeUserGestureDetection()
    // 清理全屏锁定模式相关资源
    if (this.focusLockInterval) {
      clearInterval(this.focusLockInterval)
    }
    if (this.keepOnTopInterval) {
      clearInterval(this.keepOnTopInterval)
    }
    // 清理考试安全模式
    this.disableExamSecurityMode()
  },
  methods: {
    async loadExam() {
      try {
        const examId = this.$route.params.id
        console.log('=== 开始加载考试 ===')
        console.log('考试ID:', examId)
        
        if (!examId) {
          console.error('考试ID不存在')
          alert('考试ID不存在')
          this.$router.push('/student/dashboard')
          return
        }

        // 获取学生信息
        const studentInfo = JSON.parse(localStorage.getItem('studentInfo') || '{}')
        console.log('学生信息:', studentInfo)
        
        if (!studentInfo.id) {
          console.error('学生ID不存在，需要重新登录')
          alert('请重新登录')
          this.$router.push('/student/login')
          return
        }

        console.log('准备发送API请求...')
        console.log('考试信息API:', `/api/v1/exams/${examId}`)
        console.log('题目列表API:', `/api/v1/exams/${examId}/questions`)

        // 获取考试信息和题目
        const [examResponse, questionsResponse] = await Promise.all([
          axios.get(`/api/v1/exams/${examId}`),
          axios.get(`/api/v1/exams/${examId}/questions`)
        ])

        console.log('=== API响应结果 ===')
        console.log('考试信息响应:', examResponse)
        console.log('考试信息响应状态:', examResponse.status)
        console.log('考试信息响应数据:', examResponse.data)
        
        console.log('题目列表响应:', questionsResponse)
        console.log('题目列表响应状态:', questionsResponse.status)
        console.log('题目列表响应数据:', questionsResponse.data)

        if (examResponse.data.code === 200) {
          console.log('考试信息加载成功')
          this.examInfo = examResponse.data.data
          console.log('设置考试信息:', this.examInfo)
          
          this.examStartTime = new Date()
          
          // 计算剩余时间
          const endTime = new Date(this.examInfo.endTime)
          const now = new Date()
          const examDurationMs = this.examInfo.duration * 60 * 1000
          const timeFromStart = now - this.examStartTime
          this.remainingTime = Math.max(0, Math.floor((examDurationMs - timeFromStart) / 1000))
          console.log('计算剩余时间:', this.remainingTime)
        } else {
          console.error('考试信息API返回错误:', examResponse.data)
        }

        if (questionsResponse.data.code === 200) {
          console.log('题目列表加载成功')
          this.questions = questionsResponse.data.data
          console.log('设置题目列表:', this.questions)
          console.log('题目数量:', this.questions.length)
          console.log('完整题目数据:', this.questions)
          
          // 初始化答案对象
          this.initializeAnswers()
        } else {
          console.error('题目列表API返回错误:', questionsResponse.data)
        }

        console.log('=== 最终状态 ===')
        console.log('examInfo:', this.examInfo)
        console.log('questions:', this.questions)
        console.log('questions[0]详细信息:', this.questions[0])
        if (this.questions[0] && this.questions[0].questionDetail) {
          console.log('questions[0].questionDetail:', this.questions[0].questionDetail)
        }
        console.log('currentQuestionIndex:', this.currentQuestionIndex)
        console.log('currentQuestion:', this.currentQuestion)
        console.log('currentQuestion计算结果:', this.questions[this.currentQuestionIndex])
        console.log('loading状态即将设为false')
        
        this.loading = false
      } catch (error) {
        console.error('=== 加载考试失败 ===')
        console.error('错误对象:', error)
        console.error('错误消息:', error.message)
        console.error('错误响应:', error.response)
        if (error.response) {
          console.error('响应状态:', error.response.status)
          console.error('响应数据:', error.response.data)
          console.error('响应头:', error.response.headers)
        }
        console.error('请求配置:', error.config)
        
        alert('加载考试失败，请重试')
        this.loading = false
        // 不立即跳转，让用户看到错误状态和重试按钮
        // this.$router.push('/student/dashboard')
      }
    },
    initializeAnswers() {
      this.questions.forEach(examQuestion => {
        const questionId = examQuestion.questionId
        const questionType = examQuestion.questionType?.toLowerCase()
        
        if (questionType === 'multiple') {
          this.answers[questionId] = []
        } else {
          this.answers[questionId] = ''
        }
      })
      console.log('初始化答案对象:', this.answers)
    },
    startTimer() {
      this.timer = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime--
        } else {
          this.autoSubmitExam()
        }
      }, 1000)
    },
    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      
      if (hours > 0) {
        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
      }
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    getQuestionTypeText(type) {
      const typeMap = {
        'single': '单选题',
        'multiple': '多选题',
        'judge': '判断题',
        'fill': '填空题',
        'essay': '简答题',
        // 兼容大写格式
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'TRUE_FALSE': '判断题',
        'FILL_BLANK': '填空题',
        'SHORT_ANSWER': '简答题'
      }
      return typeMap[type] || '未知题型'
    },
    isQuestionAnswered(index) {
      const examQuestion = this.questions[index]
      if (!examQuestion) return false
      
      const questionId = examQuestion.questionId
      const answer = this.answers[questionId]
      
      if (Array.isArray(answer)) {
        return answer.length > 0
      }
      return answer !== null && answer !== undefined && answer !== ''
    },
    goToQuestion(index) {
      this.currentQuestionIndex = index
    },
    previousQuestion() {
      if (this.currentQuestionIndex > 0) {
        this.currentQuestionIndex--
      }
    },
    nextQuestion() {
      if (this.currentQuestionIndex < this.questions.length - 1) {
        this.currentQuestionIndex++
      }
    },
    selectSingleChoice(optionIndex) {
      if (this.currentQuestion) {
        this.answers[this.currentQuestion.questionId] = optionIndex
      }
    },
    selectTrueFalse(value) {
      if (this.currentQuestion) {
        this.answers[this.currentQuestion.questionId] = value.toString()
      }
    },
    toggleFlag(index) {
      const flagIndex = this.flaggedQuestions.indexOf(index)
      if (flagIndex > -1) {
        this.flaggedQuestions.splice(flagIndex, 1)
      } else {
        this.flaggedQuestions.push(index)
      }
    },
    async saveAnswers() {
      try {
        // 这里可以实现答案的临时保存功能
        alert('答案已保存')
      } catch (error) {
        console.error('保存答案失败:', error)
        alert('保存答案失败')
      }
    },
    async submitExam() {
      this.loading = true
      
      try {
        // 获取学生信息
        const studentInfo = JSON.parse(localStorage.getItem('studentInfo') || '{}')
        if (!studentInfo || !studentInfo.id) {
          alert('请重新登录')
          this.$router.push('/student/login')
          return
        }
        
        // 检查考试信息
        if (!this.examInfo || !this.examInfo.id) {
          alert('考试信息不完整，请刷新页面重试')
          return
        }
        
        const submitData = {
          examId: this.examInfo.id,
          studentId: studentInfo.id,
          answers: this.answers,
          submitTime: new Date().toISOString()
        }
        
        console.log('提交考试数据:', submitData)

        const response = await axios.post('/api/v1/exams/submit', submitData)
        
        console.log('提交考试响应:', response)
        
        // 安全地检查响应
        const responseData = response && response.data ? response.data : null
        
        if (responseData && responseData.code === 200) {
          alert('考试提交成功！正在退出考试模式...')
          // 清除计时器
          if (this.timer) {
            clearInterval(this.timer)
          }
          // 退出考试安全模式
          this.disableExamSecurityMode()
          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            this.$router.push('/student/dashboard')
          }, 1500)
        } else {
          const errorMsg = responseData && responseData.message ? responseData.message : '提交失败'
          alert(errorMsg)
          // 即使提交失败也退出安全模式
          this.disableExamSecurityMode()
        }
      } catch (error) {
        console.error('提交考试失败:', error)
        
        let errorMessage = '提交考试失败，请重试'
        
        if (error && error.response && error.response.data && error.response.data.message) {
          errorMessage = `提交失败: ${error.response.data.message}`
        } else if (error && error.request) {
          errorMessage = '网络错误，请检查网络连接'
        } else if (error && error.message) {
          errorMessage = `提交失败: ${error.message}`
        }
        
        alert(errorMessage)
        // 发生错误时也退出安全模式
        this.disableExamSecurityMode()
      } finally {
        this.loading = false
        this.showSubmitConfirm = false
      }
    },
    autoSubmitExam() {
      alert('考试时间已到，系统将自动提交')
      this.submitExam()
    },
    reloadExam() {
      console.log('=== 用户点击重新加载考试 ===')
      this.loading = true
      this.questions = []
      this.answers = {}
      this.currentQuestionIndex = 0
      console.log('重置状态完成，开始重新加载...')
      this.loadExam()
    },
    // 启用考试安全模式
     enableExamSecurityMode() {
       console.log('=== 启用考试安全模式 ===')
       
       // 1. 设置用户手势监听
       this.setupUserGestureDetection()
       
       // 2. 禁用右键菜单
       this.disableContextMenu()
       
       // 3. 禁用复制粘贴和开发者工具
       this.disableCopyPasteAndDevTools()
       
       // 4. 禁用开发者工具检测
       this.disableDevTools()
       
       // 5. 监听页面失焦
       this.monitorPageFocus()
       
       // 6. 禁用文本选择
       this.disableTextSelection()
       
       // 7. 监听全屏退出
       this.monitorFullscreenExit()
       
       // 8. 禁用打印功能
       this.disablePrint()
       
       // 9. 启用全屏锁定模式（替代原来的全屏提示）
       setTimeout(() => {
         this.enableFullscreenLockMode()
         this.isFullscreenLockMode = true
       }, 1000)
       
       console.log('考试安全模式已启用，正在启动全屏锁定模式')
     },
    // 禁用考试安全模式
    disableExamSecurityMode() {
      console.log('=== 禁用考试安全模式 ===')
      
      // 禁用全屏锁定模式
      if (this.isFullscreenLockMode) {
        this.disableFullscreenLockMode()
        this.isFullscreenLockMode = false
      }
      
      // 退出全屏
      this.exitFullscreen()
      
      // 移除事件监听器
      this.removeSecurityListeners()
      
      // 恢复文本选择
      this.enableTextSelection()
      
      console.log('考试安全模式已禁用')
    },
    // 进入全屏（优化版本）
    async enterFullscreen(retryCount = 0) {
      const maxRetries = 3
      const retryDelay = 500
      
      try {
        const element = document.documentElement
        
        // 检查是否已经在全屏模式
        if (this.isCurrentlyFullscreen()) {
          console.log('已经处于全屏模式')
          this.isFullscreenActive = true
          return true
        }
        
        // 检查用户手势权限
        if (!this.hasUserGesture && retryCount === 0) {
          console.warn('需要用户手势才能进入全屏模式')
          this.showFullscreenPrompt()
          return false
        }
        
        // 重置全屏状态
        this.fullscreenRetryCount = retryCount
        
        const fullscreenOptions = {
          navigationUI: 'hide'
        }
        
        let fullscreenPromise
        if (element.requestFullscreen) {
          fullscreenPromise = element.requestFullscreen(fullscreenOptions)
        } else if (element.webkitRequestFullscreen) {
          fullscreenPromise = element.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT)
        } else if (element.mozRequestFullScreen) {
          fullscreenPromise = element.mozRequestFullScreen()
        } else if (element.msRequestFullscreen) {
          fullscreenPromise = element.msRequestFullscreen()
        }
        
        if (fullscreenPromise) {
          await fullscreenPromise
          console.log('已成功进入全屏模式')
          this.isFullscreenActive = true
          this.fullscreenRetryCount = 0
          return true
        } else {
          throw new Error('浏览器不支持全屏API')
        }
      } catch (error) {
        console.warn(`全屏进入失败 (尝试 ${retryCount + 1}/${maxRetries + 1}):`, error.message)
        
        // 如果还有重试次数，延迟后重试
        if (retryCount < maxRetries) {
          console.log(`${retryDelay}ms 后重试进入全屏...`)
          setTimeout(() => {
            this.enterFullscreen(retryCount + 1)
          }, retryDelay)
          return false
        } else {
          // 所有重试都失败，使用备用方案
          console.warn('所有全屏尝试都失败，启用备用方案')
          this.forceFullscreenFallback()
          return false
        }
      }
    },
    
    // 检查当前是否处于全屏状态
    isCurrentlyFullscreen() {
      return !!(document.fullscreenElement || 
               document.webkitFullscreenElement || 
               document.mozFullScreenElement || 
               document.msFullscreenElement)
    },
    // 全屏失败时的备用方案
    forceFullscreenFallback() {
      // 隐藏浏览器UI元素
      document.body.style.margin = '0'
      document.body.style.padding = '0'
      document.body.style.overflow = 'hidden'
      document.documentElement.style.overflow = 'hidden'
      
      // 设置页面为全窗口大小
      const examContainer = this.$el
      if (examContainer) {
        examContainer.style.position = 'fixed'
        examContainer.style.top = '0'
        examContainer.style.left = '0'
        examContainer.style.width = '100vw'
        examContainer.style.height = '100vh'
        examContainer.style.zIndex = '9999'
        examContainer.style.backgroundColor = '#fff'
      }
      
      this.$message.info('已启用强制全窗口模式')
    },
    
    // 全屏锁定模式 - 强制客户端以全屏独占模式运行
    async enableFullscreenLockMode() {
      console.log('=== 启用全屏锁定模式 ===')
      
      try {
        // 1. 首先尝试进入标准全屏模式
        const fullscreenSuccess = await this.enterFullscreen()
        
        // 2. 启用强制全屏锁定功能
        this.activateFullscreenLock()
        
        // 3. 隐藏系统UI元素
        this.hideSystemUI()
        
        // 4. 增强窗口控制
        this.enhanceWindowControl()
        
        // 5. 启用强制焦点锁定
        this.enableForceFocusLock()
        
        console.log('全屏锁定模式已启用')
        this.showNotificationMessage('已启用全屏锁定模式，考试期间将无法切换到其他应用', 'info', 3000)
        
        return true
      } catch (error) {
        console.error('启用全屏锁定模式失败:', error)
        return false
      }
    },
    
    // 激活全屏锁定功能
    activateFullscreenLock() {
      // 强制页面占满整个屏幕
      document.documentElement.style.cssText = `
        margin: 0 !important;
        padding: 0 !important;
        width: 100vw !important;
        height: 100vh !important;
        overflow: hidden !important;
        position: fixed !important;
        top: 0 !important;
        left: 0 !important;
        z-index: 999999 !important;
        background: #fff !important;
      `
      
      document.body.style.cssText = `
        margin: 0 !important;
        padding: 0 !important;
        width: 100vw !important;
        height: 100vh !important;
        overflow: hidden !important;
        position: fixed !important;
        top: 0 !important;
        left: 0 !important;
        z-index: 999999 !important;
        background: #fff !important;
      `
      
      // 设置考试容器为最高优先级
      const examContainer = this.$el
      if (examContainer) {
        examContainer.style.cssText = `
          position: fixed !important;
          top: 0 !important;
          left: 0 !important;
          width: 100vw !important;
          height: 100vh !important;
          z-index: 9999999 !important;
          background: #fff !important;
          overflow: auto !important;
        `
      }
    },
    
    // 隐藏系统UI元素
    hideSystemUI() {
      // 尝试隐藏浏览器地址栏和工具栏
      if (window.navigator && window.navigator.standalone !== undefined) {
        // iOS Safari
        window.navigator.standalone = true
      }
      
      // 隐藏滚动条
      const hideScrollbarStyle = document.createElement('style')
      hideScrollbarStyle.id = 'hide-scrollbar-style'
      hideScrollbarStyle.textContent = `
        ::-webkit-scrollbar { width: 0px; background: transparent; }
        * { scrollbar-width: none; -ms-overflow-style: none; }
        html, body { 
          scrollbar-width: none !important;
          -ms-overflow-style: none !important;
        }
      `
      document.head.appendChild(hideScrollbarStyle)
      
      // 禁用页面缩放
      const viewport = document.querySelector('meta[name="viewport"]')
      if (viewport) {
        viewport.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no')
      } else {
        const newViewport = document.createElement('meta')
        newViewport.name = 'viewport'
        newViewport.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'
        document.head.appendChild(newViewport)
      }
    },
    
    // 增强窗口控制
    enhanceWindowControl() {
      // 阻止窗口失去焦点
      this.windowBlurHandler = (e) => {
        e.preventDefault()
        e.stopPropagation()
        
        // 立即重新获取焦点
        setTimeout(() => {
          window.focus()
          document.focus()
        }, 10)
        
        console.warn('检测到窗口失焦，已强制重新获取焦点')
        this.showNotificationMessage('检测到尝试切换窗口，已阻止操作', 'warning', 2000)
        
        return false
      }
      
      // 阻止页面可见性变化
      this.visibilityLockHandler = (e) => {
        if (document.hidden) {
          e.preventDefault()
          e.stopPropagation()
          
          // 强制页面保持可见
          Object.defineProperty(document, 'hidden', {
            value: false,
            writable: false,
            configurable: false
          })
          
          Object.defineProperty(document, 'visibilityState', {
            value: 'visible',
            writable: false,
            configurable: false
          })
          
          console.warn('检测到页面隐藏，已强制保持可见')
          this.showNotificationMessage('检测到尝试隐藏页面，已阻止操作', 'warning', 2000)
        }
      }
      
      // 监听窗口事件
      window.addEventListener('blur', this.windowBlurHandler, true)
      window.addEventListener('focus', () => {
        console.log('窗口重新获得焦点')
      })
      
      document.addEventListener('visibilitychange', this.visibilityLockHandler, true)
      
      // 阻止窗口最小化
      window.addEventListener('beforeunload', (e) => {
        e.preventDefault()
        e.returnValue = '考试正在进行中，确定要离开吗？'
        return '考试正在进行中，确定要离开吗？'
      })
    },
    
    // 启用强制焦点锁定
    enableForceFocusLock() {
      // 持续监控并强制获取焦点
      this.focusLockInterval = setInterval(() => {
        if (!document.hasFocus()) {
          window.focus()
          document.focus()
          
          // 如果不在全屏状态，尝试重新进入全屏
          if (!this.isCurrentlyFullscreen()) {
            this.hasUserGesture = true
            this.enterFullscreen()
          }
        }
      }, 100) // 每100ms检查一次
      
      // 强制保持窗口在最前面
      this.keepOnTopInterval = setInterval(() => {
        try {
          // 尝试将窗口置于最前
          if (window.focus) {
            window.focus()
          }
          
          // 检查窗口大小，确保全屏
          if (window.outerWidth !== screen.width || window.outerHeight !== screen.height) {
            // 尝试调整窗口大小
            if (window.resizeTo) {
              window.resizeTo(screen.width, screen.height)
            }
            if (window.moveTo) {
              window.moveTo(0, 0)
            }
          }
        } catch (error) {
          // 忽略权限错误
        }
      }, 500) // 每500ms检查一次
    },
    
    // 禁用全屏锁定模式
    disableFullscreenLockMode() {
      console.log('=== 禁用全屏锁定模式 ===')
      
      // 清除焦点锁定定时器
      if (this.focusLockInterval) {
        clearInterval(this.focusLockInterval)
        this.focusLockInterval = null
      }
      
      if (this.keepOnTopInterval) {
        clearInterval(this.keepOnTopInterval)
        this.keepOnTopInterval = null
      }
      
      // 移除窗口控制监听器
      if (this.windowBlurHandler) {
        window.removeEventListener('blur', this.windowBlurHandler, true)
      }
      
      if (this.visibilityLockHandler) {
        document.removeEventListener('visibilitychange', this.visibilityLockHandler, true)
      }
      
      // 恢复系统UI
      this.restoreSystemUI()
      
      // 退出全屏
      this.exitFullscreen()
      
      console.log('全屏锁定模式已禁用')
    },
    
    // 恢复系统UI
    restoreSystemUI() {
      // 恢复页面样式
      document.documentElement.style.cssText = ''
      document.body.style.cssText = ''
      
      // 移除隐藏滚动条的样式
      const hideScrollbarStyle = document.getElementById('hide-scrollbar-style')
      if (hideScrollbarStyle) {
        hideScrollbarStyle.remove()
      }
      
      // 恢复视口设置
      const viewport = document.querySelector('meta[name="viewport"]')
      if (viewport) {
        viewport.setAttribute('content', 'width=device-width, initial-scale=1.0')
      }
      
      // 恢复考试容器样式
      const examContainer = this.$el
      if (examContainer) {
        examContainer.style.cssText = ''
      }
    },
    // 退出全屏
    exitFullscreen() {
      try {
        // 检查是否处于全屏状态
        const isFullscreen = document.fullscreenElement || 
                            document.webkitFullscreenElement || 
                            document.mozFullScreenElement || 
                            document.msFullscreenElement
        
        if (!isFullscreen) {
          console.log('当前不在全屏状态，无需退出')
          return
        }
        
        // 检查文档是否活跃
        if (document.hidden || !document.hasFocus()) {
          console.warn('文档不活跃，延迟退出全屏')
          // 等待文档重新激活后再退出全屏
          const handleVisibilityChange = () => {
            if (!document.hidden && document.hasFocus()) {
              document.removeEventListener('visibilitychange', handleVisibilityChange)
              this.performExitFullscreen()
            }
          }
          document.addEventListener('visibilitychange', handleVisibilityChange)
          return
        }
        
        this.performExitFullscreen()
      } catch (error) {
        console.warn('退出全屏失败:', error)
      }
    },
    
    // 执行退出全屏操作
    performExitFullscreen() {
      try {
        if (document.exitFullscreen) {
          document.exitFullscreen()
        } else if (document.webkitExitFullscreen) {
          document.webkitExitFullscreen()
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen()
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen()
        }
        console.log('已退出全屏模式')
      } catch (error) {
        console.warn('执行退出全屏操作失败:', error)
      }
    },
    // 禁用右键菜单
    disableContextMenu() {
      this.contextMenuHandler = (e) => {
        e.preventDefault()
        this.$message.warning('考试期间禁止使用右键菜单')
        return false
      }
      document.addEventListener('contextmenu', this.contextMenuHandler)
    },
    // 禁用复制粘贴和开发者工具（增强版本）
    disableCopyPasteAndDevTools() {
      this.keydownHandler = (e) => {
        // 禁用 Windows 键（左右两个）
        if (e.key === 'Meta' || e.metaKey || e.key === 'OS' || 
            e.keyCode === 91 || e.keyCode === 92 || e.which === 91 || e.which === 92) {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止使用Windows键', 'warning', 3000)
          return false
        }
        
        // 禁用 Alt+Tab 组合键 - 防止切换到其他窗口
        if (e.altKey && e.key === 'Tab') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止切换窗口', 'warning', 3000)
          return false
        }
        
        // 禁用 Alt+Esc 组合键 - 防止切换到其他窗口
        if (e.altKey && e.key === 'Escape') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止切换窗口', 'warning', 3000)
          return false
        }
        
        // 禁用 Ctrl+Alt+Del 组合键 - 防止打开任务管理器结束进程
        if (e.ctrlKey && e.altKey && e.key === 'Delete') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止使用系统快捷键', 'warning', 3000)
          return false
        }
        
        // 禁用 Ctrl+Shift+Esc 组合键 - 防止直接打开任务管理器
        if (e.ctrlKey && e.shiftKey && e.key === 'Escape') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止打开任务管理器', 'warning', 3000)
          return false
        }
        
        // 禁用 Ctrl+Esc 组合键 - 防止打开开始菜单
        if (e.ctrlKey && e.key === 'Escape') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止打开开始菜单', 'warning', 3000)
          return false
        }
        
        // 禁用 PrintScreen 键 - 禁止截图
        if (e.key === 'PrintScreen') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止截图', 'warning', 3000)
          return false
        }
        
        // 禁用 Win+PrintScreen 组合键 - 禁止截图
        if ((e.key === 'Meta' || e.metaKey || e.keyCode === 91 || e.keyCode === 92) && e.key === 'PrintScreen') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止截图', 'warning', 3000)
          return false
        }
        
        // 禁用 Ctrl+C, Ctrl+V - 防止复制粘贴（若题目不允许）
        if (e.ctrlKey && ['c', 'v'].includes(e.key.toLowerCase())) {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止复制粘贴操作', 'warning', 3000)
          return false
        }
        
        // 禁用 Ctrl+N, Ctrl+O - 防止在其他程序中新建/打开文件
        if (e.ctrlKey && ['n', 'o'].includes(e.key.toLowerCase())) {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止新建或打开文件', 'warning', 3000)
          return false
        }
        
        // 禁用其他编辑相关快捷键 Ctrl+A, Ctrl+X, Ctrl+Z, Ctrl+Y
        if (e.ctrlKey && ['a', 'x', 'z', 'y'].includes(e.key.toLowerCase())) {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止编辑操作', 'warning', 3000)
          return false
        }
        
        // 强化 ESC 键禁用（防止退出全屏）
        if (e.key === 'Escape' || e.keyCode === 27) {
          e.preventDefault()
          e.stopPropagation()
          e.stopImmediatePropagation()
          this.showNotificationMessage('考试期间禁止退出全屏模式', 'warning', 3000)
          // 立即尝试重新进入全屏
          setTimeout(() => {
            if (!this.isCurrentlyFullscreen()) {
              this.forceReturnToFullscreen()
            }
          }, 100)
          return false
        }
        
        // 禁用 F12, Ctrl+Shift+I, Ctrl+Shift+J, Ctrl+U, F11, Ctrl+Shift+C
        if (e.key === 'F12' || e.key === 'F11' ||
            (e.ctrlKey && e.shiftKey && ['i', 'j', 'c'].includes(e.key.toLowerCase())) ||
            (e.ctrlKey && e.key.toLowerCase() === 'u')) {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止打开开发者工具', 'warning', 3000)
          return false
        }
        
        // 禁用 Ctrl+P (打印)
        if (e.ctrlKey && e.key.toLowerCase() === 'p') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止打印', 'warning', 3000)
          return false
        }
        
        // 禁用 Alt+F4 (关闭窗口)
        if (e.altKey && e.key === 'F4') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止关闭窗口', 'warning', 3000)
          return false
        }
      }
      
      // 添加 keyup 事件监听器来捕获 Windows 键和 PrintScreen 键释放
      this.keyupHandler = (e) => {
        // 捕获 Windows 键释放
        if (e.key === 'Meta' || e.metaKey || e.key === 'OS' || 
            e.keyCode === 91 || e.keyCode === 92 || e.which === 91 || e.which === 92) {
          e.preventDefault()
          e.stopPropagation()
          return false
        }
        
        // 捕获 PrintScreen 键释放 - 防止截图
        if (e.key === 'PrintScreen') {
          e.preventDefault()
          e.stopPropagation()
          this.showNotificationMessage('考试期间禁止截图', 'warning', 2000)
          return false
        }
      }
      
      this.copyHandler = (e) => {
        e.preventDefault()
        this.showNotificationMessage('考试期间禁止复制操作', 'warning', 3000)
        return false
      }
      
      this.pasteHandler = (e) => {
        e.preventDefault()
        this.showNotificationMessage('考试期间禁止粘贴操作', 'warning', 3000)
        return false
      }
      
      document.addEventListener('keydown', this.keydownHandler)
      document.addEventListener('keyup', this.keyupHandler)
      document.addEventListener('copy', this.copyHandler)
      document.addEventListener('paste', this.pasteHandler)
    },
    // 禁用开发者工具
    disableDevTools() {
      // 检测开发者工具是否打开
      this.devToolsChecker = setInterval(() => {
        const threshold = 160
        if (window.outerHeight - window.innerHeight > threshold || 
            window.outerWidth - window.innerWidth > threshold) {
          this.showNotificationMessage('检测到开发者工具，请关闭后继续考试', 'error', 5000)
          // 可以选择强制提交考试或其他处理
        }
      }, 1000)
    },
    // 监听页面失焦（优化版本）
    monitorPageFocus() {
      const vm = this // 保存this引用
      let lastAlertTime = 0
      let focusRecoveryTimeout = null
      let isRecoveringFocus = false
      const alertDelay = 1000 // 1秒内不重复弹出提示
      const recoveryDelay = 300 // 焦点恢复延迟
      
      const showAlertAndForceFullscreen = (message, eventType) => {
         const now = Date.now()
         
         // 防抖处理：如果正在恢复焦点，忽略此次事件
         if (isRecoveringFocus) {
           console.log('正在恢复焦点中，忽略失焦事件')
           return
         }
         
         // 检查是否在短时间内重复触发
         if (now - lastAlertTime > alertDelay) {
           lastAlertTime = now
           
           // 记录失焦次数和类型
           vm.focusLossCount = (vm.focusLossCount || 0) + 1
           vm.focusLossHistory = vm.focusLossHistory || []
           vm.focusLossHistory.push({ time: now, type: eventType })
           
           console.warn(`页面失焦次数: ${vm.focusLossCount}, 类型: ${eventType}`)
           
           // 显示通知
           vm.showNotificationMessage(message, 'warning', 3000)
           
           // 清除之前的恢复定时器
           if (focusRecoveryTimeout) {
             clearTimeout(focusRecoveryTimeout)
           }
           
           // 延迟执行焦点恢复，避免立即触发冲突
           focusRecoveryTimeout = setTimeout(() => {
             try {
               isRecoveringFocus = true
               
               // 尝试让窗口获得焦点
               if (window.focus) {
                 window.focus()
               }
               
               // 强制返回全屏
               vm.forceReturnToFullscreen()
               
               // 重置恢复状态
               setTimeout(() => {
                 isRecoveringFocus = false
               }, 1000)
               
             } catch (error) {
               console.error('焦点恢复过程中出错:', error)
               isRecoveringFocus = false
             }
           }, recoveryDelay)
         }
       }
      
      this.blurHandler = () => {
        console.log('页面失去焦点')
        showAlertAndForceFullscreen('检测到离开考试页面，正在强制返回全屏模式', 'blur')
      }
      
      this.visibilityChangeHandler = () => {
        if (document.hidden) {
          console.log('页面变为不可见')
          showAlertAndForceFullscreen('检测到切换标签页或窗口，正在强制返回全屏模式', 'visibility')
        } else {
          // 页面重新可见时，检查全屏状态
          console.log('页面重新可见')
          setTimeout(() => {
            if (!vm.isCurrentlyFullscreen()) {
              console.log('页面可见但未全屏，尝试恢复全屏')
              vm.forceReturnToFullscreen()
            }
          }, 200)
        }
      }
      
      // 添加焦点恢复监听器
      this.focusHandler = () => {
        if (isRecoveringFocus) {
          console.log('焦点已恢复')
          // 检查全屏状态
          setTimeout(() => {
            if (!vm.isCurrentlyFullscreen()) {
              vm.enterFullscreen()
            }
          }, 100)
        }
      }
      
      window.addEventListener('blur', this.blurHandler)
      window.addEventListener('focus', this.focusHandler)
      document.addEventListener('visibilitychange', this.visibilityChangeHandler)
    },
    // 禁用文本选择
    disableTextSelection() {
      document.body.style.userSelect = 'none'
      document.body.style.webkitUserSelect = 'none'
      document.body.style.mozUserSelect = 'none'
      document.body.style.msUserSelect = 'none'
    },
    // 恢复文本选择
    enableTextSelection() {
      document.body.style.userSelect = ''
      document.body.style.webkitUserSelect = ''
      document.body.style.mozUserSelect = ''
      document.body.style.msUserSelect = ''
    },
    // 监听全屏退出
    monitorFullscreenExit() {
      this.fullscreenChangeHandler = () => {
        const isFullscreen = !!(document.fullscreenElement || 
                               document.webkitFullscreenElement || 
                               document.mozFullScreenElement || 
                               document.msFullscreenElement)
        
        if (!isFullscreen && this.isFullscreenActive) {
          this.isFullscreenActive = false
          
          // 记录退出全屏的次数
          this.fullscreenExitCount = (this.fullscreenExitCount || 0) + 1
          console.warn('全屏退出次数:', this.fullscreenExitCount)
          
          // 如果多次尝试退出全屏，显示警告
          if (this.fullscreenExitCount >= 3) {
            this.showNotificationMessage('多次尝试退出全屏，这可能被视为违规行为', 'error', 5000)
          } else {
            this.showNotificationMessage('检测到退出全屏，请重新进入全屏模式继续考试', 'warning', 4000)
            // 显示全屏提示对话框
            this.showFullscreenPrompt()
          }
        } else if (isFullscreen) {
          this.isFullscreenActive = true
          console.log('已进入全屏模式')
        }
      }
      
      // 监听所有可能的全屏变化事件
      document.addEventListener('fullscreenchange', this.fullscreenChangeHandler)
      document.addEventListener('webkitfullscreenchange', this.fullscreenChangeHandler)
      document.addEventListener('mozfullscreenchange', this.fullscreenChangeHandler)
      document.addEventListener('MSFullscreenChange', this.fullscreenChangeHandler)
      
      // 额外监听窗口大小变化
      this.resizeHandler = () => {
        if (window.innerHeight !== screen.height || window.innerWidth !== screen.width) {
          // 如果窗口大小不是全屏，尝试重新全屏
          setTimeout(() => {
            this.enterFullscreen()
          }, 500)
        }
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    // 禁用打印功能
    disablePrint() {
      // 禁用打印样式
      const style = document.createElement('style')
      style.innerHTML = '@media print { body { display: none !important; } }'
      document.head.appendChild(style)
      
      // 监听打印事件
      this.printHandler = (e) => {
        e.preventDefault()
        this.showNotificationMessage('考试期间禁止打印', 'warning', 3000)
        return false
      }
      
      window.addEventListener('beforeprint', this.printHandler)
       window.addEventListener('afterprint', this.printHandler)
     },
     
     // 移除打印限制
     removePrintRestrictions() {
       // 移除打印样式限制
       const printStyles = document.querySelectorAll('style')
       printStyles.forEach(style => {
         if (style.innerHTML.includes('@media print')) {
           style.remove()
         }
       })
       
       // 移除打印事件监听器
       if (this.printHandler) {
         window.removeEventListener('beforeprint', this.printHandler)
         window.removeEventListener('afterprint', this.printHandler)
         this.printHandler = null
       }
     },
     
     // 设置用户手势检测
    setupUserGestureDetection() {
      const detectGesture = () => {
        this.hasUserGesture = true
        console.log('检测到用户手势，可以进入全屏模式')
        // 移除手势检测监听器
        document.removeEventListener('click', detectGesture)
        document.removeEventListener('keydown', detectGesture)
        document.removeEventListener('touchstart', detectGesture)
      }
      
      document.addEventListener('click', detectGesture, { once: true })
      document.addEventListener('keydown', detectGesture, { once: true })
      document.addEventListener('touchstart', detectGesture, { once: true })
    },
    
    // 显示全屏提示（优化版本）
    showFullscreenPrompt() {
      this.showFullscreenDialog = true
      this.showNotificationMessage('请点击"进入全屏"按钮继续考试', 'warning')
      
      // 增强用户手势检测
      this.enhanceUserGestureDetection()
    },
    
    // 隐藏全屏提示对话框
    hideFullscreenPrompt() {
      this.showFullscreenDialog = false
      this.removeUserGestureDetection()
    },
    
    // 增强用户手势检测
    enhanceUserGestureDetection() {
      console.log('启用增强用户手势检测')
      
      // 监听多种用户交互事件
      const gestureEvents = ['click', 'keydown', 'touchstart', 'mousedown']
      
      gestureEvents.forEach(eventType => {
        document.addEventListener(eventType, this.handleUserGesture, { once: false, passive: true })
      })
      
      // 设置手势检测超时
      this.gestureDetectionTimeout = setTimeout(() => {
        console.log('手势检测超时，自动设置用户手势标志')
        this.hasUserGesture = true
      }, 3000)
    },
    
    // 移除用户手势检测
    removeUserGestureDetection() {
      const gestureEvents = ['click', 'keydown', 'touchstart', 'mousedown']
      
      gestureEvents.forEach(eventType => {
        document.removeEventListener(eventType, this.handleUserGesture)
      })
      
      if (this.gestureDetectionTimeout) {
        clearTimeout(this.gestureDetectionTimeout)
        this.gestureDetectionTimeout = null
      }
    },
    
    // 处理用户手势
    handleUserGesture(event) {
      console.log('检测到用户手势:', event.type)
      this.hasUserGesture = true
      
      // 如果是在全屏提示状态下的点击，尝试进入全屏
      if (this.showFullscreenDialog && (event.type === 'click' || event.type === 'touchstart')) {
        setTimeout(() => {
          this.enterFullscreen().then(success => {
            if (success) {
              this.hideFullscreenPrompt()
            }
          })
        }, 100)
      }
    },
    
    // 用户点击进入全屏（优化版本）
     async userEnterFullscreen() {
       this.hasUserGesture = true
       this.hideFullscreenPrompt()
       const success = await this.enterFullscreen()
       
       if (!success) {
         console.warn('用户手动进入全屏失败，启用恢复机制')
         this.startFullscreenRecovery()
       }
     },
     
     // 强制回到页面并进入全屏（优化版本）
     async forceReturnToFullscreen() {
       console.log('检测到窗口失焦，强制回到页面并进入全屏')
       
       try {
         // 尝试让窗口获得焦点
         if (window.focus) {
           window.focus()
         }
         
         // 检查是否在全屏模式
         if (!this.isCurrentlyFullscreen()) {
           // 设置用户手势标志，允许进入全屏
           this.hasUserGesture = true
           
           // 尝试进入全屏
           const success = await this.enterFullscreen()
           
           if (!success) {
             console.warn('强制进入全屏失败，启用恢复机制')
             this.startFullscreenRecovery()
           }
         }
         
         // 记录违规行为
         this.focusViolationCount = (this.focusViolationCount || 0) + 1
         if (this.focusViolationCount >= 5) {
           this.showNotificationMessage('多次离开考试页面，这可能被视为严重违规行为！', 'error')
         }
         
       } catch (error) {
         console.error('强制返回全屏过程中出错:', error)
         this.startFullscreenRecovery()
       }
     },
     
     // 启动全屏恢复机制
     startFullscreenRecovery() {
       console.log('启动全屏恢复机制')
       
       // 清除之前的恢复定时器
       if (this.fullscreenRecoveryTimer) {
         clearInterval(this.fullscreenRecoveryTimer)
       }
       
       let recoveryAttempts = 0
       const maxRecoveryAttempts = 10
       const recoveryInterval = 1000 // 每秒尝试一次
       
       this.fullscreenRecoveryTimer = setInterval(async () => {
         recoveryAttempts++
         console.log(`全屏恢复尝试 ${recoveryAttempts}/${maxRecoveryAttempts}`)
         
         // 检查是否已经恢复全屏
         if (this.isCurrentlyFullscreen()) {
           console.log('全屏状态已恢复')
           clearInterval(this.fullscreenRecoveryTimer)
           this.fullscreenRecoveryTimer = null
           return
         }
         
         // 尝试恢复全屏
         try {
           this.hasUserGesture = true
           const success = await this.enterFullscreen()
           
           if (success) {
             console.log('全屏恢复成功')
             clearInterval(this.fullscreenRecoveryTimer)
             this.fullscreenRecoveryTimer = null
             return
           }
         } catch (error) {
           console.warn(`恢复尝试 ${recoveryAttempts} 失败:`, error.message)
         }
         
         // 如果达到最大尝试次数，停止恢复并显示提示
         if (recoveryAttempts >= maxRecoveryAttempts) {
           console.warn('全屏恢复失败，显示手动提示')
           clearInterval(this.fullscreenRecoveryTimer)
           this.fullscreenRecoveryTimer = null
           this.showFullscreenPrompt()
         }
       }, recoveryInterval)
     },
     
     // 停止全屏恢复机制
     stopFullscreenRecovery() {
       if (this.fullscreenRecoveryTimer) {
         clearInterval(this.fullscreenRecoveryTimer)
         this.fullscreenRecoveryTimer = null
         console.log('全屏恢复机制已停止')
       }
     },
     
     // 显示右上角通知
     showNotificationMessage(message, type = 'warning', duration = 5000) {
       this.notificationMessage = message
       this.notificationType = type
       this.showNotification = true
       
       // 自动关闭通知
       if (duration > 0) {
         setTimeout(() => {
           this.closeNotification()
         }, duration)
       }
     },
     
     // 关闭通知
     closeNotification() {
       this.showNotification = false
       this.notificationMessage = ''
     },
     
     // 获取通知图标
     getNotificationIcon() {
       switch (this.notificationType) {
         case 'success':
           return 'icon-success'
         case 'error':
           return 'icon-error'
         case 'info':
           return 'icon-info'
         case 'warning':
         default:
           return 'icon-warning'
       }
     },
    
     // 移除安全监听器
    removeSecurityListeners() {
      // 移除事件监听器
      if (this.contextMenuHandler) {
        document.removeEventListener('contextmenu', this.contextMenuHandler)
      }
      if (this.keydownHandler) {
        document.removeEventListener('keydown', this.keydownHandler)
      }
      if (this.copyHandler) {
        document.removeEventListener('copy', this.copyHandler)
      }
      if (this.pasteHandler) {
        document.removeEventListener('paste', this.pasteHandler)
      }
      if (this.blurHandler) {
        window.removeEventListener('blur', this.blurHandler)
      }
      if (this.visibilityChangeHandler) {
        document.removeEventListener('visibilitychange', this.visibilityChangeHandler)
      }
      if (this.fullscreenChangeHandler) {
        document.removeEventListener('fullscreenchange', this.fullscreenChangeHandler)
        document.removeEventListener('webkitfullscreenchange', this.fullscreenChangeHandler)
        document.removeEventListener('mozfullscreenchange', this.fullscreenChangeHandler)
        document.removeEventListener('MSFullscreenChange', this.fullscreenChangeHandler)
      }
      if (this.resizeHandler) {
        window.removeEventListener('resize', this.resizeHandler)
      }
      if (this.printHandler) {
        window.removeEventListener('beforeprint', this.printHandler)
        window.removeEventListener('afterprint', this.printHandler)
      }
      
      // 清除定时器
      if (this.devToolsChecker) {
        clearInterval(this.devToolsChecker)
      }
    }
  }
}
</script>

<style scoped>
.exam-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

/* 考试头部 */
.exam-header {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.exam-info {
  flex: 1;
}

.exam-title {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.exam-description {
  color: #7f8c8d;
  font-size: 16px;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.exam-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.exam-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #34495e;
  font-size: 14px;
}

/* 计时器 */
.exam-timer {
  text-align: center;
  min-width: 200px;
}

.timer-display {
  margin-bottom: 12px;
}

.time-label {
  display: block;
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.time-value {
  font-size: 32px;
  font-weight: bold;
  color: #27ae60;
  font-family: 'Courier New', monospace;
}

.time-value.time-warning {
  color: #f39c12;
}

.time-value.time-danger {
  color: #e74c3c;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #ecf0f1;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #27ae60, #2ecc71);
  transition: width 1s ease;
}

/* 题目导航 */
.question-nav {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-title {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 16px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
  gap: 8px;
  margin-bottom: 16px;
}

.nav-item {
  width: 40px;
  height: 40px;
  border: 2px solid #bdc3c7;
  background: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-item:hover {
  border-color: #3498db;
  background: #ecf0f1;
}

.nav-item.current {
  border-color: #3498db;
  background: #3498db;
  color: white;
}

.nav-item.answered {
  border-color: #27ae60;
  background: #27ae60;
  color: white;
}

.nav-item.flagged {
  border-color: #f39c12;
  background: #f39c12;
  color: white;
}

.nav-legend {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #7f8c8d;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid;
}

.legend-dot.current {
  border-color: #3498db;
  background: #3498db;
}

.legend-dot.answered {
  border-color: #27ae60;
  background: #27ae60;
}

.legend-dot.flagged {
  border-color: #f39c12;
  background: #f39c12;
}

/* 主要内容区域 */
.exam-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.question-area {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.question-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ecf0f1;
}

.question-number {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.question-type {
  background: #3498db;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: bold;
}

.question-score {
  color: #e74c3c;
  font-weight: bold;
}

.flag-button {
  margin-left: auto;
  background: none;
  border: 1px solid #bdc3c7;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.flag-button:hover {
  border-color: #f39c12;
  color: #f39c12;
}

.flag-button.flagged {
  background: #f39c12;
  border-color: #f39c12;
  color: white;
}

.question-content {
  margin-bottom: 24px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  color: #2c3e50;
  margin-bottom: 20px;
}

/* 答题选项 */
.answer-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.option-item:hover {
  border-color: #3498db;
  background: #f8f9fa;
}

.option-item input {
  margin-right: 12px;
  margin-top: 2px;
}

.option-label {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  flex: 1;
}

.option-letter {
  background: #ecf0f1;
  color: #7f8c8d;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.option-text {
  line-height: 1.5;
  color: #2c3e50;
}

/* 输入框 */
.answer-input {
  margin-top: 16px;
}

.fill-input,
.short-answer-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.fill-input:focus,
.short-answer-input:focus {
  outline: none;
  border-color: #3498db;
}

/* 题目导航按钮 */
.question-navigation {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: 2px solid #3498db;
  background: white;
  color: #3498db;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.nav-btn:hover:not(:disabled) {
  background: #3498db;
  color: white;
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 考试操作区域 */
.exam-actions {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.action-stats {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #ecf0f1;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #7f8c8d;
  font-size: 14px;
}

.stat-value {
  font-weight: bold;
  color: #2c3e50;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn {
  background: #95a5a6;
  color: white;
}

.save-btn:hover {
  background: #7f8c8d;
}

.submit-btn {
  background: #27ae60;
  color: white;
}

.submit-btn:hover {
  background: #229954;
}

/* 模态框 */
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
}

.submit-modal,
.fullscreen-modal {
  background: white;
  border-radius: 12px;
  padding: 24px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-header h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 20px;
}

.modal-content {
  margin-bottom: 20px;
}

.submit-stats p {
  margin: 8px 0;
  line-height: 1.5;
}

.warning-text {
  color: #e74c3c;
  font-weight: bold;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.modal-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #95a5a6;
  color: white;
}

.cancel-btn:hover {
  background: #7f8c8d;
}

.confirm-btn {
  background: #e74c3c;
  color: white;
}

.confirm-btn:hover {
  background: #c0392b;
}

/* 加载状态 */
.loading-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #7f8c8d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #ecf0f1;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

/* 空状态 */
.empty-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.retry-btn {
  margin-top: 16px;
  padding: 10px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s ease;
}

.retry-btn:hover {
  background: #2980b9;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #ecf0f1;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 右上角通知样式 */
.notification-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 10000;
  max-width: 400px;
}

.notification-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-bottom: 10px;
  padding: 16px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  animation: slideInRight 0.3s ease-out;
  border-left: 4px solid #ffa500;
}

.notification-item.warning {
  border-left-color: #ffa500;
}

.notification-item.error {
  border-left-color: #ff4757;
}

.notification-item.success {
  border-left-color: #2ed573;
}

.notification-item.info {
  border-left-color: #3742fa;
}

.notification-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.notification-icon {
  width: 20px;
  height: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

.notification-icon.icon-warning::before {
  content: '⚠️';
  font-size: 16px;
}

.notification-icon.icon-error::before {
  content: '❌';
  font-size: 16px;
}

.notification-icon.icon-success::before {
  content: '✅';
  font-size: 16px;
}

.notification-icon.icon-info::before {
  content: 'ℹ️';
  font-size: 16px;
}

.notification-message {
  color: #333;
  font-size: 14px;
  line-height: 1.4;
  word-break: break-word;
}

.notification-close {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  padding: 0;
  margin-left: 12px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-close:hover {
  color: #666;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .exam-content {
    grid-template-columns: 1fr;
  }
  
  .exam-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .exam-timer {
    align-self: flex-start;
  }
}

@media (max-width: 768px) {
  .exam-container {
    padding: 10px;
  }
  
  .exam-header,
  .question-nav,
  .question-area,
  .exam-actions {
    padding: 16px;
  }
  
  .nav-grid {
    grid-template-columns: repeat(auto-fill, minmax(35px, 1fr));
  }
  
  .nav-item {
    width: 35px;
    height: 35px;
    font-size: 12px;
  }
  
  .question-header {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .flag-button {
    margin-left: 0;
    order: -1;
    width: 100%;
    justify-content: center;
  }
}
</style>