<template>
  <Layout>
    <div class="content-header">
      <h2>学生考试情况</h2>
      <p class="subtitle">查看所有学生的考试记录和成绩</p>
    </div>

    <!-- 筛选和搜索区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-group">
          <label>考试筛选:</label>
          <select v-model="selectedExamId" @change="loadExamRecords">
            <option value="">全部考试</option>
            <option v-for="exam in exams" :key="exam.id" :value="exam.id">
              {{ exam.title }}
            </option>
          </select>
        </div>
        
        <div class="filter-group">
          <label>成绩范围:</label>
          <select v-model="scoreFilter" @change="filterRecords">
            <option value="">全部成绩</option>
            <option value="excellent">优秀 (90-100)</option>
            <option value="good">良好 (80-89)</option>
            <option value="pass">及格 (60-79)</option>
            <option value="fail">不及格 (0-59)</option>
          </select>
        </div>
        
        <div class="filter-group">
          <label>提交状态:</label>
          <select v-model="statusFilter" @change="filterRecords">
            <option value="">全部状态</option>
            <option value="0">未开始</option>
            <option value="1">进行中</option>
            <option value="2">已提交</option>
            <option value="3">超时提交</option>
          </select>
        </div>
        
        <div class="search-group">
          <input 
            type="text" 
            v-model="searchKeyword" 
            @input="filterRecords"
            placeholder="搜索学生姓名或学号..."
            class="search-input"
          >
        </div>
        
        <button @click="exportResults" class="btn secondary">
          <i class="icon">📊</i>
          导出结果
        </button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview" v-if="statistics">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-info">
          <h3>参考人数</h3>
          <p class="stat-number">{{ statistics.totalStudents }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <h3>提交人数</h3>
          <p class="stat-number">{{ statistics.submittedCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <h3>平均分</h3>
          <p class="stat-number">{{ statistics.averageScore?.toFixed(1) || 0 }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎯</div>
        <div class="stat-info">
          <h3>及格率</h3>
          <p class="stat-number">{{ statistics.passRate?.toFixed(1) || 0 }}%</p>
        </div>
      </div>
    </div>

    <!-- 考试记录列表 -->
    <div class="exam-records">
      <div class="records-header">
        <h3>考试记录 ({{ filteredRecords.length }})</h3>
        <div class="header-actions">
          <button @click="loadExamRecords" class="btn secondary small">
            <i class="icon">🔄</i>
            刷新
          </button>
        </div>
      </div>
      
      <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="filteredRecords.length === 0" class="no-data">
        <div class="no-data-icon">📝</div>
        <p>暂无考试记录</p>
      </div>
      
      <div v-else class="records-table">
        <table>
          <thead>
            <tr>
              <th>学号</th>
              <th>姓名</th>
              <th>考试名称</th>
              <th>开始时间</th>
              <th>提交时间</th>
              <th>用时</th>
              <th>得分</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in paginatedRecords" :key="record.id" class="record-row">
              <td class="student-number">{{ record.studentNumber }}</td>
              <td class="student-name">{{ record.studentName }}</td>
              <td class="exam-title">{{ getExamTitle(record.examId) }}</td>
              <td class="start-time">{{ formatDateTime(record.startTime) }}</td>
              <td class="submit-time">
                <span v-if="record.submitTime">{{ formatDateTime(record.submitTime) }}</span>
                <span v-else class="not-submitted">未提交</span>
              </td>
              <td class="duration">
                <span v-if="record.submitTime && record.startTime">
                  {{ calculateDuration(record.startTime, record.submitTime) }}
                </span>
                <span v-else>-</span>
              </td>
              <td class="score">
                <span v-if="record.score !== null" :class="getScoreClass(record.score)">
                  {{ record.score }}
                </span>
                <span v-else class="no-score">未评分</span>
              </td>
              <td class="status">
                <span :class="getStatusClass(record.status)">
                  {{ getStatusText(record.status) }}
                </span>
              </td>
              <td class="actions">
                <div class="action-buttons">
                  <button @click="viewDetails(record)" class="btn primary small" title="查看详情">
                    <i class="icon">👁️</i>
                  </button>
                  <button 
                    @click="editScore(record)" 
                    class="btn secondary small" 
                    title="编辑分数"
                    :disabled="record.status !== 2 && record.status !== 3"
                  >
                    <i class="icon">✏️</i>
                  </button>
                  <button 
                    @click="resetExam(record)" 
                    class="btn warning small" 
                    title="重新考试"
                  >
                    <i class="icon">🔄</i>
                  </button>
                  <button 
                    @click="deleteRecord(record)" 
                    class="btn danger small" 
                    title="删除记录"
                  >
                    <i class="icon">🗑️</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <button 
            @click="currentPage = Math.max(1, currentPage - 1)" 
            :disabled="currentPage === 1"
            class="btn secondary small"
          >
            上一页
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button 
            @click="currentPage = Math.min(totalPages, currentPage + 1)" 
            :disabled="currentPage === totalPages"
            class="btn secondary small"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 详情模态框 -->
    <div v-if="showDetailsModal" class="modal-overlay" @click="closeDetailsModal">
      <div class="modal-content large-modal" @click.stop>
        <div class="modal-header">
          <h3>考试详情</h3>
          <button @click="closeDetailsModal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-body" v-if="selectedRecord">
          <div class="detail-section">
            <h4>基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <label>学生姓名:</label>
                <span>{{ selectedRecord.studentName }}</span>
              </div>
              <div class="detail-item">
                <label>学号:</label>
                <span>{{ selectedRecord.studentNumber }}</span>
              </div>
              <div class="detail-item">
                <label>考试名称:</label>
                <span>{{ getExamTitle(selectedRecord.examId) }}</span>
              </div>
              <div class="detail-item">
                <label>开始时间:</label>
                <span>{{ formatDateTime(selectedRecord.startTime) }}</span>
              </div>
              <div class="detail-item">
                <label>提交时间:</label>
                <span v-if="selectedRecord.submitTime">{{ formatDateTime(selectedRecord.submitTime) }}</span>
                <span v-else class="not-submitted">未提交</span>
              </div>
              <div class="detail-item">
                <label>考试用时:</label>
                <span v-if="selectedRecord.submitTime && selectedRecord.startTime">
                  {{ calculateDuration(selectedRecord.startTime, selectedRecord.submitTime) }}
                </span>
                <span v-else>-</span>
              </div>
              <div class="detail-item">
                <label>得分:</label>
                <span v-if="selectedRecord.score !== null" :class="getScoreClass(selectedRecord.score)">
                  {{ selectedRecord.score }} 分
                </span>
                <span v-else class="no-score">未评分</span>
              </div>
              <div class="detail-item">
                <label>状态:</label>
                <span :class="getStatusClass(selectedRecord.status)">
                  {{ getStatusText(selectedRecord.status) }}
                </span>
              </div>
            </div>
          </div>
          
          <!-- 答题详情 -->
          <div class="detail-section" v-if="selectedRecord.answers">
            <h4>答题详情</h4>
            <div class="answers-list">
              <div v-for="(answer, questionId) in selectedRecord.answers" :key="questionId" class="answer-item">
                <div class="question-info">
                  <span class="question-label">题目 {{ questionId }}:</span>
                  <span class="answer-content">{{ formatAnswer(answer) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeDetailsModal" class="btn secondary">关闭</button>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'
import axios from 'axios'

export default {
  name: 'StudentExamResults',
  components: {
    Layout
  },
  data() {
    return {
      // 数据列表
      exams: [],
      examRecords: [],
      filteredRecords: [],
      
      // 筛选条件
      selectedExamId: '',
      scoreFilter: '',
      statusFilter: '',
      searchKeyword: '',
      
      // 分页
      currentPage: 1,
      pageSize: 20,
      
      // 状态
      loading: false,
      
      // 统计数据
      statistics: null,
      
      // 详情模态框
      showDetailsModal: false,
      selectedRecord: null
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredRecords.length / this.pageSize)
    },
    paginatedRecords() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredRecords.slice(start, end)
    }
  },
  mounted() {
    this.loadExams()
    this.loadExamRecords()
  },
  methods: {
    // 加载考试列表
    async loadExams() {
      try {
        const response = await axios.get('/api/v1/exams')
        if (response.data.code === 200) {
          this.exams = response.data.data.content || response.data.data
        }
      } catch (error) {
        console.error('加载考试列表失败:', error)
        alert('加载考试列表失败')
      }
    },
    
    // 加载考试记录
    async loadExamRecords() {
      this.loading = true
      try {
        // 构建查询参数
        const params = {
          page: this.currentPage - 1,
          size: this.pageSize,
          sortBy: 'createdAt',
          sortDir: 'desc'
        }
        
        if (this.selectedExamId) {
          params.examId = this.selectedExamId
        }
        
        if (this.searchKeyword.trim()) {
          params.studentName = this.searchKeyword.trim()
        }
        
        if (this.statusFilter) {
          params.status = this.statusFilter
        }

        const response = await axios.get('/api/v1/exams/records', { params })
        if (response.data.code === 200) {
          this.examRecords = response.data.data.content
          this.totalRecords = response.data.data.totalElements
          this.filteredRecords = [...this.examRecords]
          this.filterRecords()
          this.calculateStatistics()
        }
      } catch (error) {
        console.error('加载考试记录失败:', error)
        alert('加载考试记录失败')
      } finally {
        this.loading = false
      }
    },
    
    // 筛选记录
    filterRecords() {
      let filtered = [...this.examRecords]
      
      // 按成绩范围筛选
      if (this.scoreFilter) {
        filtered = filtered.filter(record => {
          if (record.score === null) return false
          const score = record.score
          switch (this.scoreFilter) {
            case 'excellent': return score >= 90
            case 'good': return score >= 80 && score < 90
            case 'pass': return score >= 60 && score < 80
            case 'fail': return score < 60
            default: return true
          }
        })
      }
      
      // 按提交状态筛选
      if (this.statusFilter) {
        const statusValue = parseInt(this.statusFilter)
        filtered = filtered.filter(record => record.status === statusValue)
      }
      
      // 按关键词搜索
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.trim().toLowerCase()
        filtered = filtered.filter(record => 
          record.studentName?.toLowerCase().includes(keyword) ||
          record.studentNumber?.toLowerCase().includes(keyword)
        )
      }
      
      this.filteredRecords = filtered
      this.currentPage = 1 // 重置到第一页
      
      // 重新计算统计数据
      this.calculateStatistics()
    },
    
    // 计算统计数据
    calculateStatistics() {
      const records = this.filteredRecords
      const submittedRecords = records.filter(r => r.status === 1)
      const scoredRecords = records.filter(r => r.score !== null)
      
      this.statistics = {
        totalStudents: records.length,
        submittedCount: submittedRecords.length,
        averageScore: scoredRecords.length > 0 ? 
          scoredRecords.reduce((sum, r) => sum + r.score, 0) / scoredRecords.length : 0,
        passRate: scoredRecords.length > 0 ? 
          (scoredRecords.filter(r => r.score >= 60).length / scoredRecords.length) * 100 : 0
      }
    },
    
    // 获取考试标题
    getExamTitle(examId) {
      const exam = this.exams.find(e => e.id === examId)
      return exam ? exam.title : `考试 ${examId}`
    },
    
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    // 计算考试用时
    calculateDuration(startTime, endTime) {
      if (!startTime || !endTime) return '-'
      const start = new Date(startTime)
      const end = new Date(endTime)
      const diffMs = end - start
      const diffMins = Math.floor(diffMs / 60000)
      const hours = Math.floor(diffMins / 60)
      const mins = diffMins % 60
      return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
    },
    
    // 获取成绩样式类
    getScoreClass(score) {
      if (score >= 90) return 'score-excellent'
      if (score >= 80) return 'score-good'
      if (score >= 60) return 'score-pass'
      return 'score-fail'
    },
    
    // 获取状态样式类
    getStatusClass(status) {
      switch (status) {
        case 0: return 'status-not-started'
        case 1: return 'status-in-progress'
        case 2: return 'status-submitted'
        case 3: return 'status-timeout'
        default: return 'status-unknown'
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 0: return '未开始'
        case 1: return '进行中'
        case 2: return '已提交'
        case 3: return '超时提交'
        default: return '未知状态'
      }
    },
    
    // 查看详情
    viewDetails(record) {
      this.selectedRecord = record
      this.showDetailsModal = true
    },
    
    // 关闭详情模态框
    closeDetailsModal() {
      this.showDetailsModal = false
      this.selectedRecord = null
    },
    
    // 格式化答案
    formatAnswer(answer) {
      if (Array.isArray(answer)) {
        return answer.join(', ')
      }
      return answer || '未作答'
    },
    
    // 编辑分数
    editScore(record) {
      if (record.status !== 2 && record.status !== 3) {
        alert('只能编辑已完成的考试记录分数')
        return
      }
      
      const newScore = prompt(`请输入新分数（当前分数：${record.score || 0}）：`, record.score || 0)
      if (newScore === null) return // 用户取消
      
      const score = parseFloat(newScore)
      if (isNaN(score) || score < 0) {
        alert('请输入有效的分数')
        return
      }
      
      this.updateExamRecordScore(record.id, score)
    },
    
    // 更新考试记录分数
    async updateExamRecordScore(recordId, newScore) {
      try {
        const response = await axios.put(`http://localhost:8080/api/v1/exams/records/${recordId}/score`, {
          score: newScore
        })
        
        if (response.data.code === 200) {
          alert('分数更新成功')
          // 更新本地数据
          const record = this.examRecords.find(r => r.id === recordId)
          if (record) {
            record.score = newScore
          }
          this.filterRecords() // 重新筛选和计算统计
        } else {
          alert(response.data.message || '分数更新失败')
        }
      } catch (error) {
        console.error('更新分数失败:', error)
        alert('分数更新失败：' + (error.response?.data?.message || error.message))
      }
    },
    
    // 重新考试
    resetExam(record) {
      const confirmed = confirm(`确定要重置学生 ${record.studentName} 的考试记录吗？\n\n重置后该学生可以重新参加考试，原有答题记录将被清除。`)
      if (confirmed) {
        this.resetExamRecord(record.id)
      }
    },
    
    // 重置考试记录
    async resetExamRecord(recordId) {
      try {
        const response = await axios.post(`http://localhost:8080/api/v1/exams/records/${recordId}/reset`)
        
        if (response.data.code === 200) {
          alert('考试记录重置成功')
          // 更新本地数据
          const record = this.examRecords.find(r => r.id === recordId)
          if (record) {
            record.status = 0 // 未开始
            record.startTime = null
            record.submitTime = null
            record.score = 0
            record.correctCount = 0
            record.wrongCount = 0
            record.unansweredCount = 0
          }
          this.filterRecords() // 重新筛选和计算统计
        } else {
          alert(response.data.message || '重置失败')
        }
      } catch (error) {
        console.error('重置考试记录失败:', error)
        alert('重置失败：' + (error.response?.data?.message || error.message))
      }
    },
    
    // 删除记录
    deleteRecord(record) {
      const confirmed = confirm(`确定要删除学生 ${record.studentName} 的考试记录吗？\n\n删除后将无法恢复！`)
      if (confirmed) {
        this.deleteExamRecord(record.id)
      }
    },
    
    // 删除考试记录
    async deleteExamRecord(recordId) {
      try {
        const response = await axios.delete(`http://localhost:8080/api/v1/exams/records/${recordId}`)
        
        if (response.data.code === 200) {
          alert('考试记录删除成功')
          // 从本地数据中移除
          const index = this.examRecords.findIndex(r => r.id === recordId)
          if (index !== -1) {
            this.examRecords.splice(index, 1)
          }
          this.filterRecords() // 重新筛选和计算统计
        } else {
          alert(response.data.message || '删除失败')
        }
      } catch (error) {
        console.error('删除考试记录失败:', error)
        alert('删除失败：' + (error.response?.data?.message || error.message))
      }
    },

    // 导出结果
    exportResults() {
      // 简单的CSV导出
      const headers = ['学号', '姓名', '考试名称', '开始时间', '提交时间', '用时', '得分', '状态']
      const csvContent = [
        headers.join(','),
        ...this.filteredRecords.map(record => [
          record.studentNumber,
          record.studentName,
          this.getExamTitle(record.examId),
          this.formatDateTime(record.startTime),
          record.submitTime ? this.formatDateTime(record.submitTime) : '未提交',
          record.submitTime && record.startTime ? this.calculateDuration(record.startTime, record.submitTime) : '-',
          record.score !== null ? record.score : '未评分',
          this.getStatusText(record.status)
        ].join(','))
      ].join('\n')
      
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `学生考试记录_${new Date().toISOString().slice(0, 10)}.csv`
      link.click()
    }
  }
}
</script>

<style scoped>
/* 页面头部 */
.content-header {
  margin-bottom: 2rem;
}

.content-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.subtitle {
  color: #64748b;
  font-size: 1rem;
  margin: 0;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  margin-bottom: 2rem;
}

.filter-row {
  display: flex;
  gap: 1rem;
  align-items: end;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #374151;
}

.filter-group select {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
  min-width: 120px;
}

.search-group {
  flex: 1;
  min-width: 200px;
}

.search-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  border-radius: 12px;
}

.stat-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #64748b;
  font-weight: 500;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

/* 考试记录区域 */
.exam-records {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.records-header h3 {
  margin: 0;
  color: #1e293b;
  font-size: 1.2rem;
}

/* 表格样式 */
.records-table {
  overflow-x: auto;
}

.records-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.records-table th,
.records-table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.records-table th {
  background: #f8fafc;
  font-weight: 600;
  color: #374151;
}

.record-row:hover {
  background: #f8fafc;
}

/* 成绩样式 */
.score-excellent {
  color: #059669;
  font-weight: 600;
}

.score-good {
  color: #0891b2;
  font-weight: 600;
}

.score-pass {
  color: #d97706;
  font-weight: 600;
}

.score-fail {
  color: #dc2626;
  font-weight: 600;
}

.no-score {
  color: #9ca3af;
  font-style: italic;
}

/* 状态样式 */
.status-not-started {
  color: #9ca3af;
  font-weight: 500;
}

.status-in-progress {
  color: #d97706;
  font-weight: 500;
}

.status-submitted {
  color: #059669;
  font-weight: 500;
}

.status-timeout {
  color: #dc2626;
  font-weight: 500;
}

.status-unknown {
  color: #6b7280;
  font-weight: 500;
}

.status-not-submitted {
  color: #dc2626;
  font-weight: 500;
}

.not-submitted {
  color: #9ca3af;
  font-style: italic;
}

/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn.primary {
  background: #667eea;
  color: white;
}

.btn.secondary {
  background: #e5e7eb;
  color: #374151;
}

.btn.small {
  padding: 0.5rem 1rem;
  font-size: 0.8rem;
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* 加载和空数据状态 */
.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 2rem;
  color: #64748b;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #e5e7eb;
  border-top: 2px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.no-data {
  text-align: center;
  padding: 3rem;
  color: #9ca3af;
}

.no-data-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.page-info {
  font-size: 0.9rem;
  color: #64748b;
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
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  width: 90vw;
  max-height: 80vh;
  overflow-y: auto;
}

.large-modal {
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
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #9ca3af;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #374151;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
}

/* 详情区域 */
.detail-section {
  margin-bottom: 2rem;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1.1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-item label {
  font-size: 0.85rem;
  color: #64748b;
  font-weight: 500;
}

.detail-item span {
  font-size: 0.9rem;
  color: #1e293b;
}

/* 答题详情 */
.answers-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.answer-item {
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 6px;
  border-left: 3px solid #667eea;
}

.question-info {
  display: flex;
  gap: 0.5rem;
}

.question-label {
  font-weight: 500;
  color: #374151;
  min-width: 80px;
}

.answer-content {
  color: #1e293b;
}
</style>