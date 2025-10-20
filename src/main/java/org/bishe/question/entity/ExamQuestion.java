package org.bishe.question.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试题目关联实体类
 */
@Entity
@Table(name = "exam_questions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;
    
    @Column(name = "score", precision = 5, scale = 2, nullable = false)
    private BigDecimal score;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    @JsonIgnore
    private Exam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // 构造函数
    public ExamQuestion() {}
    
    public ExamQuestion(Long examId, Long questionId, Integer questionOrder, BigDecimal score) {
        this.examId = examId;
        this.questionId = questionId;
        this.questionOrder = questionOrder;
        this.score = score;
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
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public Integer getQuestionOrder() {
        return questionOrder;
    }
    
    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }
    
    public BigDecimal getScore() {
        return score;
    }
    
    public void setScore(BigDecimal score) {
        this.score = score;
    }
    
    public void setQuestionScore(BigDecimal score) {
        this.score = score;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Exam getExam() {
        return exam;
    }
    
    public void setExam(Exam exam) {
        this.exam = exam;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    @Override
    public String toString() {
        return "ExamQuestion{" +
                "id=" + id +
                ", examId=" + examId +
                ", questionId=" + questionId +
                ", questionOrder=" + questionOrder +
                ", score=" + score +
                ", createdAt=" + createdAt +
                '}';
    }
}