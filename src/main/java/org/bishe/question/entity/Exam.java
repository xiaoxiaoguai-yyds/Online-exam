package org.bishe.question.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试实体类
 */
@Entity
@Table(name = "exams")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Exam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "duration", nullable = false)
    private Integer duration; // 考试时长(分钟)
    
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore = BigDecimal.valueOf(100.00);
    
    @Column(name = "pass_score", precision = 5, scale = 2)
    private BigDecimal passScore = BigDecimal.valueOf(60.00);
    
    @Column(name = "question_count")
    private Integer questionCount = 0;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用, 1-启用, 2-已结束
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ExamQuestion> examQuestions;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ExamRecord> examRecords;
    
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
    public Exam() {}
    
    public Exam(String title, String description, LocalDateTime startTime, LocalDateTime endTime, Integer duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public BigDecimal getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
    
    public BigDecimal getPassScore() {
        return passScore;
    }
    
    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }
    
    public Integer getQuestionCount() {
        return questionCount;
    }
    
    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
    
    public List<ExamQuestion> getExamQuestions() {
        return examQuestions;
    }
    
    public void setExamQuestions(List<ExamQuestion> examQuestions) {
        this.examQuestions = examQuestions;
    }
    
    public List<ExamRecord> getExamRecords() {
        return examRecords;
    }
    
    public void setExamRecords(List<ExamRecord> examRecords) {
        this.examRecords = examRecords;
    }
    
    // 业务方法
    
    /**
     * 检查考试是否正在进行中
     */
    public boolean isInProgress() {
        LocalDateTime now = LocalDateTime.now();
        return status == 1 && now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    /**
     * 检查考试是否已结束
     */
    public boolean isFinished() {
        return status == 2 || LocalDateTime.now().isAfter(endTime);
    }
    
    /**
     * 检查考试是否未开始
     */
    public boolean isNotStarted() {
        return status == 1 && LocalDateTime.now().isBefore(startTime);
    }
    
    /**
     * 获取考试剩余时间(分钟)
     */
    public long getRemainingMinutes() {
        if (isFinished()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return 0;
        }
        LocalDateTime examEndTime = now.plusMinutes(duration);
        if (examEndTime.isAfter(endTime)) {
            examEndTime = endTime;
        }
        return java.time.Duration.between(now, examEndTime).toMinutes();
    }
    
    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", totalScore=" + totalScore +
                ", passScore=" + passScore +
                ", questionCount=" + questionCount +
                ", status=" + status +
                ", createdBy=" + createdBy +
                '}';
    }
}