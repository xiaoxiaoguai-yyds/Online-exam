<template>
  <Layout>
    <div class="content-header">
      <div class="header-content">
        <div>
          <h2>仪表盘</h2>
          <p class="subtitle">欢迎来到后台管理系统</p>
        </div>
        <div class="header-actions">
          <button @click="refreshDashboard" :disabled="loadingStats" class="refresh-btn">
            <span v-if="loadingStats">🔄 刷新中...</span>
            <span v-else>🔄 刷新数据</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-info">
          <h3>用户总数</h3>
          <p class="stat-number">{{ dashboardStats.totalUsers || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎓</div>
        <div class="stat-info">
          <h3>学生总数</h3>
          <p class="stat-number">{{ dashboardStats.totalStudents || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📝</div>
        <div class="stat-info">
          <h3>考试总数</h3>
          <p class="stat-number">{{ dashboardStats.totalExams || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <h3>完成率</h3>
          <p class="stat-number">{{ dashboardStats.completionRate || 0 }}%</p>
        </div>
      </div>
    </div>

    <!-- 详细统计信息 -->
    <div class="detailed-stats">
      <div class="stats-section">
        <h3>考试统计</h3>
        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-label">进行中考试：</span>
            <span class="stat-value">{{ dashboardStats.activeExams || 0 }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">题目总数：</span>
            <span class="stat-value">{{ dashboardStats.totalQuestions || 0 }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">平均分：</span>
            <span class="stat-value">{{ dashboardStats.averageScore || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据可视化图表 -->
    <div class="charts-section">
      <div class="charts-grid">
        <!-- 考试状态分布图 -->
        <div class="chart-card">
          <h3>考试状态分布</h3>
          <div class="chart-container">
            <canvas ref="examStatusChart" width="400" height="300"></canvas>
          </div>
        </div>

        <!-- 题目类型分布图 -->
        <div class="chart-card">
          <h3>题目类型分布</h3>
          <div class="chart-container">
            <canvas ref="questionTypeChart" width="400" height="300"></canvas>
          </div>
        </div>

        <!-- 用户活跃度趋势图 -->
        <div class="chart-card full-width">
          <h3>最近活动</h3>
          <div class="activities-list">
            <div v-if="recentActivities.recentExams && recentActivities.recentExams.length > 0" class="activity-section">
              <h4>最近考试</h4>
              <div v-for="exam in recentActivities.recentExams" :key="exam.id" class="activity-item">
                <span class="activity-title">{{ exam.title }}</span>
                <span class="activity-time">{{ formatDate(exam.createdAt) }}</span>
              </div>
            </div>
            <div v-if="recentActivities.recentRecords && recentActivities.recentRecords.length > 0" class="activity-section">
              <h4>最近考试记录</h4>
              <div v-for="record in recentActivities.recentRecords" :key="record.id" class="activity-item">
                <span class="activity-title">{{ record.studentName }} - {{ record.examTitle }}</span>
                <span class="activity-score">{{ record.score }}分</span>
                <span class="activity-time">{{ formatDate(record.completedAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h3>快速操作</h3>
      <div class="action-buttons">
        <button class="action-btn primary" @click="showCreateExamModal = true">
          <i class="icon">📝</i>
          <span>创建考试</span>
        </button>
        <button class="action-btn secondary" @click="showExamList = !showExamList">
          <i class="icon">📋</i>
          <span>考试管理</span>
        </button>
        <button class="action-btn tertiary">
          <i class="icon">📊</i>
          <span>查看报表</span>
        </button>
      </div>
    </div>

    <!-- 考试管理区域 -->
    <div v-if="showExamList" class="exam-management">
      <div class="exam-header">
        <h3>考试管理</h3>
        <div class="exam-filters">
          <select v-model="statusFilter" @change="loadExams">
            <option value="">全部状态</option>
            <option value="0">未开始</option>
            <option value="1">进行中</option>
            <option value="2">已结束</option>
          </select>
          <input 
            type="text" 
            v-model="searchTitle" 
            @input="searchExams" 
            placeholder="搜索考试标题..."
            class="search-input"
          >
        </div>
      </div>
      
      <div class="exam-list">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="exams.length === 0" class="no-data">暂无考试数据</div>
        <div v-else>
          <div v-for="exam in exams" :key="exam.id" class="exam-item">
            <div class="exam-info">
              <h4>{{ exam.title }}</h4>
              <p class="exam-desc">{{ exam.description }}</p>
              <div class="exam-meta">
                <span class="exam-time">开始时间: {{ formatDateTime(exam.startTime) }}</span>
                <span class="exam-duration">时长: {{ exam.duration }}分钟</span>
                <span class="exam-score">总分: {{ exam.totalScore }}分</span>
                <span :class="['exam-status', getStatusClass(exam.status)]">{{ getStatusText(exam.status) }}</span>
              </div>
            </div>
            <div class="exam-actions">
              <button class="btn-small primary" @click="editExam(exam)">编辑</button>
              <button class="btn-small secondary" @click="viewStatistics(exam.id)">统计</button>
              <button 
                class="btn-small" 
                :class="exam.status === 1 ? 'danger' : 'success'"
                @click="toggleExamStatus(exam)"
              >
                {{ exam.status === 1 ? '结束' : '启用' }}
              </button>
              <button class="btn-small danger" @click="deleteExam(exam.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination">
        <button 
          v-for="page in totalPages" 
          :key="page" 
          :class="['page-btn', { active: page === currentPage }]"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
      </div>
    </div>

    <!-- 创建/编辑考试模态框 -->
    <div v-if="showCreateExamModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingExam ? '编辑考试' : '创建考试' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <form @submit.prevent="saveExam" class="exam-form">
          <div class="form-group">
            <label>考试标题</label>
            <input type="text" v-model="examForm.title" required class="form-input">
          </div>
          <div class="form-group">
            <label>考试描述</label>
            <textarea v-model="examForm.description" class="form-textarea"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>开始时间</label>
              <input type="datetime-local" v-model="examForm.startTime" required class="form-input">
            </div>
            <div class="form-group">
              <label>结束时间</label>
              <input type="datetime-local" v-model="examForm.endTime" required class="form-input">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>考试时长(分钟)</label>
              <input type="number" v-model="examForm.duration" required class="form-input">
            </div>
            <div class="form-group">
              <label>总分</label>
              <input type="number" v-model="examForm.totalScore" required class="form-input">
            </div>
          </div>
          <div class="form-group">
            <label>及格分数</label>
            <input type="number" v-model="examForm.passScore" required class="form-input">
          </div>
          <div class="form-actions">
            <button type="button" class="btn secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn primary">{{ editingExam ? '更新' : '创建' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 统计模态框 -->
    <div v-if="showStatisticsModal" class="modal-overlay" @click="closeStatisticsModal">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>考试统计</h3>
          <button class="close-btn" @click="closeStatisticsModal">×</button>
        </div>
        <div v-if="statistics" class="statistics-content">
          <div class="stats-overview">
            <div class="stat-item">
              <span class="stat-label">参与人数</span>
              <span class="stat-value">{{ statistics.totalParticipants }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">完成人数</span>
              <span class="stat-value">{{ statistics.completedCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">通过人数</span>
              <span class="stat-value">{{ statistics.passedCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">完成率</span>
              <span class="stat-value">{{ statistics.completionRate.toFixed(1) }}%</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">通过率</span>
              <span class="stat-value">{{ statistics.passRate.toFixed(1) }}%</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">平均分</span>
              <span class="stat-value">{{ statistics.averageScore || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'
import axios from 'axios'
import { dashboardApi } from '../api/dashboard.js'

export default {
  name: 'Dashboard',
  components: {
    Layout
  },
  data() {
    return {
      // 仪表盘统计数据
      dashboardStats: {},
      userStats: {},
      examStats: {},
      questionStats: {},
      recentActivities: {},
      loadingStats: false,
      
      // 考试管理相关
      showExamList: false,
      showCreateExamModal: false,
      showStatisticsModal: false,
      exams: [],
      loading: false,
      statusFilter: '',
      searchTitle: '',
      currentPage: 1,
      totalPages: 1,
      pageSize: 10,
      
      // 考试表单
      editingExam: null,
      examForm: {
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        duration: 120,
        totalScore: 100,
        passScore: 60
      },
      
      // 统计数据
      statistics: null
    }
  },
  mounted() {
    this.loadDashboardData()
    this.loadExams()
  },
  methods: {
    // 加载仪表盘数据
    async loadDashboardData() {
      this.loadingStats = true
      try {
        // 并行加载所有统计数据
        const [dashboardResponse, userResponse, examResponse, questionResponse, activitiesResponse] = await Promise.all([
          dashboardApi.getDashboardStats(),
          dashboardApi.getUserStats(),
          dashboardApi.getExamStats(),
          dashboardApi.getQuestionStats(),
          dashboardApi.getRecentActivities()
        ])

        this.dashboardStats = dashboardResponse.data.data || {}
        this.userStats = userResponse.data.data || {}
        this.examStats = examResponse.data.data || {}
        this.questionStats = questionResponse.data.data || {}
        this.recentActivities = activitiesResponse.data.data || {}

        console.log('仪表盘数据加载成功:', {
          dashboard: this.dashboardStats,
          user: this.userStats,
          exam: this.examStats,
          question: this.questionStats,
          activities: this.recentActivities
        })

        // 数据加载完成后绘制图表
        this.$nextTick(() => {
          this.drawCharts()
        })
      } catch (error) {
        console.error('加载仪表盘数据失败:', error)
        this.$message?.error('加载仪表盘数据失败')
      } finally {
        this.loadingStats = false
      }
    },

    // 刷新仪表盘数据
    refreshDashboard() {
      this.loadDashboardData()
    },

    // 绘制图表
    drawCharts() {
      this.drawExamStatusChart()
      this.drawQuestionTypeChart()
    },

    // 绘制考试状态分布图
    drawExamStatusChart() {
      const canvas = this.$refs.examStatusChart
      if (!canvas) return

      const ctx = canvas.getContext('2d')
      const examStats = this.examStats

      // 清除画布
      ctx.clearRect(0, 0, canvas.width, canvas.height)

      // 数据
      const data = [
        { label: '进行中', value: examStats.activeCount || 0, color: '#28a745' },
        { label: '已结束', value: examStats.completedCount || 0, color: '#6c757d' },
        { label: '未开始', value: examStats.pendingCount || 0, color: '#ffc107' }
      ]

      this.drawPieChart(ctx, data, canvas.width, canvas.height)
    },

    // 绘制题目类型分布图
    drawQuestionTypeChart() {
      const canvas = this.$refs.questionTypeChart
      if (!canvas) return

      const ctx = canvas.getContext('2d')
      const questionStats = this.questionStats

      // 清除画布
      ctx.clearRect(0, 0, canvas.width, canvas.height)

      // 数据
      const data = [
        { label: '单选题', value: questionStats.singleChoiceCount || 0, color: '#007bff' },
        { label: '多选题', value: questionStats.multipleChoiceCount || 0, color: '#28a745' },
        { label: '判断题', value: questionStats.trueFalseCount || 0, color: '#ffc107' },
        { label: '填空题', value: questionStats.fillBlankCount || 0, color: '#dc3545' }
      ]

      this.drawPieChart(ctx, data, canvas.width, canvas.height)
    },

    // 绘制饼图
    drawPieChart(ctx, data, width, height) {
      const centerX = width / 2
      const centerY = height / 2
      const radius = Math.min(width, height) / 3

      let total = data.reduce((sum, item) => sum + item.value, 0)
      if (total === 0) {
        // 如果没有数据，显示提示
        ctx.fillStyle = '#666'
        ctx.font = '16px Arial'
        ctx.textAlign = 'center'
        ctx.fillText('暂无数据', centerX, centerY)
        return
      }

      let currentAngle = -Math.PI / 2

      // 绘制饼图
      data.forEach(item => {
        const sliceAngle = (item.value / total) * 2 * Math.PI

        ctx.beginPath()
        ctx.moveTo(centerX, centerY)
        ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle)
        ctx.closePath()
        ctx.fillStyle = item.color
        ctx.fill()

        // 绘制标签
        const labelAngle = currentAngle + sliceAngle / 2
        const labelX = centerX + Math.cos(labelAngle) * (radius + 30)
        const labelY = centerY + Math.sin(labelAngle) * (radius + 30)

        ctx.fillStyle = '#333'
        ctx.font = '12px Arial'
        ctx.textAlign = 'center'
        ctx.fillText(`${item.label}: ${item.value}`, labelX, labelY)

        currentAngle += sliceAngle
      })
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    // 加载考试列表
    async loadExams() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage - 1,
          size: this.pageSize
        }
        
        if (this.statusFilter) {
          params.status = this.statusFilter
        }
        
        const response = await axios.get('/api/v1/exams', { params })
        this.exams = response.data.content || []
        this.totalPages = response.data.totalPages || 1
      } catch (error) {
        console.error('加载考试列表失败:', error)
        this.$message?.error('加载考试列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 搜索考试
    async searchExams() {
      if (!this.searchTitle.trim()) {
        this.loadExams()
        return
      }
      
      this.loading = true
      try {
        const params = {
          page: 0,
          size: this.pageSize,
          title: this.searchTitle
        }
        
        const response = await axios.get('/api/v1/exams', { params })
        this.exams = response.data.content || []
        this.totalPages = response.data.totalPages || 1
        this.currentPage = 1
      } catch (error) {
        console.error('搜索考试失败:', error)
        this.$message?.error('搜索考试失败')
      } finally {
        this.loading = false
      }
    },
    
    // 创建考试
    async createExam() {
      try {
        const examData = {
          ...this.examForm,
          createdBy: 1 // 假设当前用户ID为1
        }
        
        await axios.post('/api/v1/exams', examData)
        this.$message?.success('考试创建成功')
        this.closeModal()
        this.loadExams()
      } catch (error) {
        console.error('创建考试失败:', error)
        this.$message?.error('创建考试失败')
      }
    },
    
    // 更新考试
    async updateExam() {
      try {
        await axios.put(`/api/v1/exams/${this.editingExam.id}`, this.examForm)
        this.$message?.success('考试更新成功')
        this.closeModal()
        this.loadExams()
      } catch (error) {
        console.error('更新考试失败:', error)
        this.$message?.error('更新考试失败')
      }
    },
    
    // 保存考试
    saveExam() {
      if (this.editingExam) {
        this.updateExam()
      } else {
        this.createExam()
      }
    },
    
    // 编辑考试
    editExam(exam) {
      this.editingExam = exam
      this.examForm = {
        ...exam,
        startTime: this.formatDateTimeForInput(exam.startTime),
        endTime: this.formatDateTimeForInput(exam.endTime)
      }
      this.showCreateExamModal = true
    },
    
    // 删除考试
    async deleteExam(examId) {
      if (!confirm('确定要删除这个考试吗？')) {
        return
      }
      
      try {
        await axios.delete(`/api/v1/exams/${examId}`)
        this.$message?.success('考试删除成功')
        this.loadExams()
      } catch (error) {
        console.error('删除考试失败:', error)
        this.$message?.error('删除考试失败')
      }
    },
    
    // 切换考试状态
    async toggleExamStatus(exam) {
      const newStatus = exam.status === 1 ? 2 : 1
      const action = newStatus === 1 ? '启用' : '结束'
      
      if (!confirm(`确定要${action}这个考试吗？`)) {
        return
      }
      
      try {
        await axios.put(`/api/v1/exams/${exam.id}/status`, { status: newStatus })
        this.$message?.success(`考试${action}成功`)
        this.loadExams()
      } catch (error) {
        console.error(`${action}考试失败:`, error)
        this.$message?.error(`${action}考试失败`)
      }
    },
    
    // 查看统计
    async viewStatistics(examId) {
      try {
        const response = await axios.get(`/api/v1/exams/${examId}/statistics`)
        this.statistics = response.data
        this.showStatisticsModal = true
      } catch (error) {
        console.error('获取统计数据失败:', error)
        this.$message?.error('获取统计数据失败')
      }
    },
    
    // 分页
    changePage(page) {
      this.currentPage = page
      this.loadExams()
    },
    
    // 关闭模态框
    closeModal() {
      this.showCreateExamModal = false
      this.editingExam = null
      this.examForm = {
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        duration: 120,
        totalScore: 100,
        passScore: 60
      }
    },
    
    // 关闭统计模态框
    closeStatisticsModal() {
      this.showStatisticsModal = false
      this.statistics = null
    },
    
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      return new Date(dateTime).toLocaleString('zh-CN')
    },
    
    // 格式化日期时间用于输入框
    formatDateTimeForInput(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toISOString().slice(0, 16)
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '未开始',
        1: '进行中',
        2: '已结束'
      }
      return statusMap[status] || '未知'
    },
    
    // 获取状态样式类
    getStatusClass(status) {
      const classMap = {
        0: 'pending',
        1: 'active',
        2: 'finished'
      }
      return classMap[status] || ''
    }
  }
}
</script>

<style scoped>
.content-header {
  margin-bottom: 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-header h2 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.subtitle {
  color: #64748b;
  margin: 0;
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.refresh-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.refresh-btn:hover:not(:disabled) {
  background: #0056b3;
}

.refresh-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 1rem;
  opacity: 0.8;
}

.stat-info h3 {
  margin: 0 0 0.5rem 0;
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-number {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e293b;
}

/* 详细统计信息 */
.detailed-stats {
  margin: 30px 0;
}

.stats-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.stats-section h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.stats-row {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.detailed-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detailed-stats .stat-label {
  color: #666;
  font-weight: 500;
}

.detailed-stats .stat-value {
  color: #007bff;
  font-weight: bold;
  font-size: 1.1rem;
}

/* 图表区域 */
.charts-section {
  margin: 30px 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-card h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.chart-container canvas {
  max-width: 100%;
  height: auto;
}

/* 活动列表 */
.activities-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.activity-section h4 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.1rem;
  border-bottom: 2px solid #007bff;
  padding-bottom: 5px;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-title {
  color: #333;
  font-weight: 500;
  flex: 1;
}

.activity-score {
  color: #28a745;
  font-weight: bold;
  margin: 0 10px;
}

.activity-time {
  color: #666;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .activities-list {
    grid-template-columns: 1fr;
  }
}

/* 快速操作 */
.quick-actions {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.quick-actions h3 {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1.2rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.action-btn.secondary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.action-btn.tertiary {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 考试管理样式 */
.exam-management {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  margin-top: 2rem;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.exam-header h3 {
  margin: 0;
  color: #1e293b;
  font-size: 1.2rem;
}

.exam-filters {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.exam-filters select,
.search-input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
}

.search-input {
  min-width: 200px;
}

.exam-list {
  margin-bottom: 1.5rem;
}

.loading,
.no-data {
  text-align: center;
  padding: 2rem;
  color: #64748b;
  font-size: 1rem;
}

.exam-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 1rem;
  transition: all 0.3s ease;
}

.exam-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.exam-info {
  flex: 1;
}

.exam-info h4 {
  margin: 0 0 0.5rem 0;
  color: #1e293b;
  font-size: 1.1rem;
}

.exam-desc {
  margin: 0 0 1rem 0;
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.4;
}

.exam-meta {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  font-size: 0.85rem;
}

.exam-meta span {
  color: #64748b;
}

.exam-status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 500;
}

.exam-status.pending {
  background: #fef3c7;
  color: #92400e;
}

.exam-status.active {
  background: #d1fae5;
  color: #065f46;
}

.exam-status.finished {
  background: #e5e7eb;
  color: #374151;
}

.exam-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn-small {
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-small.primary {
  background: #667eea;
  color: white;
}

.btn-small.secondary {
  background: #64748b;
  color: white;
}

.btn-small.success {
  background: #10b981;
  color: white;
}

.btn-small.danger {
  background: #ef4444;
  color: white;
}

.btn-small:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

.page-btn {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.page-btn:hover {
  background: #f3f4f6;
}

.page-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 模态框样式 */
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

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 0;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.1);
}

.modal-content.large {
  max-width: 800px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  color: #1e293b;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #64748b;
  padding: 0;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #1e293b;
}

/* 表单样式 */
.exam-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #374151;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn.primary {
  background: #667eea;
  color: white;
}

.btn.secondary {
  background: #e5e7eb;
  color: #374151;
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* 统计内容样式 */
.statistics-content {
  padding: 1.5rem;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 8px;
}

.stat-label {
  display: block;
  color: #64748b;
  font-size: 0.85rem;
  margin-bottom: 0.5rem;
}

.stat-value {
  display: block;
  color: #1e293b;
  font-size: 1.5rem;
  font-weight: 700;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-main {
    flex-direction: column;
  }
  
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
  
  .content {
    padding: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .exam-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .exam-filters {
    flex-direction: column;
  }
  
  .search-input {
    min-width: auto;
  }
  
  .exam-item {
    flex-direction: column;
    gap: 1rem;
  }
  
  .exam-actions {
    justify-content: flex-start;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
  
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>