package org.bishe.question.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录实体类
 */
@Entity
@Table(name = "exam_records")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "student_number", length = 20)
    private String studentNumber;
    
    @Column(name = "student_name", length = 50)
    private String studentName;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "submit_time")
    private LocalDateTime submitTime;
    
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;
    
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
    
    @Column(name = "correct_count")
    private Integer correctCount = 0;
    
    @Column(name = "wrong_count")
    private Integer wrongCount = 0;
    
    @Column(name = "unanswered_count")
    private Integer unansweredCount = 0;
    
    @Column(name = "status")
    private Integer status = 0; // 0-未开始, 1-进行中, 2-已提交, 3-超时提交
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    @JsonIgnore
    private Student student;
    
    @OneToMany(mappedBy = "examRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StudentAnswer> studentAnswers;
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public ExamRecord() {}
    
    public ExamRecord(Long examId, Long studentId) {
        this.examId = examId;
        this.studentId = studentId;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getExamId() {
        return examId;
    }
    
    public void setExamId(Long examId) {
        this.examId = examId;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public LocalDateTime getSubmitTime() {
        return submitTime;
    }
    
    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }
    
    public BigDecimal getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
    
    public BigDecimal getScore() {
        return score;
    }
    
    public void setScore(BigDecimal score) {
        this.score = score;
    }
    
    public Integer getCorrectCount() {
        return correctCount != null ? correctCount : 0;
    }
    
    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }
    
    public Integer getWrongCount() {
        return wrongCount != null ? wrongCount : 0;
    }
    
    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }
    
    public Integer getUnansweredCount() {
        return unansweredCount != null ? unansweredCount : 0;
    }
    
    public void setUnansweredCount(Integer unansweredCount) {
        this.unansweredCount = unansweredCount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Exam getExam() {
        return exam;
    }
    
    public void setExam(Exam exam) {
        this.exam = exam;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }
    
    public void setStudentAnswers(List<StudentAnswer> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }
    
    // 业务方法
    
    /**
     * 开始考试
     */
    public void startExam() {
        this.startTime = LocalDateTime.now();
        this.status = 1; // 进行中
    }
    
    /**
     * 提交考试
     */
    public void submitExam() {
        this.submitTime = LocalDateTime.now();
        this.endTime = this.submitTime;
        this.status = 2; // 已提交
        
        // 计算考试时长
        if (this.startTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(this.startTime, this.submitTime).toMinutes();
        }
    }
    
    /**
     * 超时提交
     */
    public void timeoutSubmit() {
        this.submitTime = LocalDateTime.now();
        this.endTime = this.submitTime;
        this.status = 3; // 超时提交
        
        // 计算考试时长
        if (this.startTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(this.startTime, this.submitTime).toMinutes();
        }
    }
    
    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return status == 2 || status == 3;
    }
    
    /**
     * 检查是否正在进行中
     */
    public boolean isInProgress() {
        return status == 1;
    }
    
    /**
     * 计算正确率
     */
    public double getAccuracy() {
        int correct = correctCount != null ? correctCount : 0;
        int wrong = wrongCount != null ? wrongCount : 0;
        int totalAnswered = correct + wrong;
        if (totalAnswered == 0) {
            return 0.0;
        }
        return (double) correct / totalAnswered * 100;
    }
    
    /**
     * 获取得分率
     */
    public double getScoreRate() {
        if (totalScore == null || totalScore.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        if (score == null) {
            return 0.0;
        }
        return score.divide(totalScore, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
    }
    
    @Override
    public String toString() {
        return "ExamRecord{" +
                "id=" + id +
                ", examId=" + examId +
                ", studentId=" + studentId +
                ", startTime=" + startTime +
                ", submitTime=" + submitTime +
                ", score=" + score +
                ", totalScore=" + totalScore +
                ", status=" + status +
                ", correctCount=" + correctCount +
                ", wrongCount=" + wrongCount +
                ", unansweredCount=" + unansweredCount +
                '}';
    }
}