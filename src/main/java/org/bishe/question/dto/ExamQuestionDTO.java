package org.bishe.question.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试题目DTO
 */
public class ExamQuestionDTO {
    private Long id;
    private Long examId;
    private Long questionId;
    private Integer questionOrder;
    private BigDecimal score;
    private LocalDateTime createdAt;
    
    // 题目信息
    private String questionTitle;
    private String questionContent;
    private String questionType;
    private String questionDifficulty;
    private String questionOptions;
    private String questionCorrectAnswers;
    private String questionCorrectAnswer;
    private String questionTags;
    
    // 构造函数
    public ExamQuestionDTO() {}
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getQuestionTitle() {
        return questionTitle;
    }
    
    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
    
    public String getQuestionContent() {
        return questionContent;
    }
    
    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public String getQuestionDifficulty() {
        return questionDifficulty;
    }
    
    public void setQuestionDifficulty(String questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }
    
    public String getQuestionOptions() {
        return questionOptions;
    }
    
    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }
    
    public String getQuestionCorrectAnswers() {
        return questionCorrectAnswers;
    }
    
    public void setQuestionCorrectAnswers(String questionCorrectAnswers) {
        this.questionCorrectAnswers = questionCorrectAnswers;
    }
    
    public String getQuestionCorrectAnswer() {
        return questionCorrectAnswer;
    }
    
    public void setQuestionCorrectAnswer(String questionCorrectAnswer) {
        this.questionCorrectAnswer = questionCorrectAnswer;
    }
    
    public String getQuestionTags() {
        return questionTags;
    }
    
    public void setQuestionTags(String questionTags) {
        this.questionTags = questionTags;
    }
}