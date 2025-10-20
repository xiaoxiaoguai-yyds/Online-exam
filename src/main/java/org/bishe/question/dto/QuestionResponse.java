package org.bishe.question.dto;

import org.bishe.question.entity.Question;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目响应DTO
 */
public class QuestionResponse {
    
    private Long id;
    
    private String title;
    
    private String content;
    
    private Question.QuestionType type;
    
    private Question.Difficulty difficulty;
    
    private List<String> options;
    
    private List<String> correctAnswers;
    
    private String correctAnswer;
    
    private List<String> tags;
    
    private Integer status;
    
    private Long createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    // 构造函数
    public QuestionResponse() {}
    
    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.type = question.getType();
        this.difficulty = question.getDifficulty();
        this.status = question.getStatus();
        this.createdBy = question.getCreatedBy();
        this.createdTime = question.getCreatedTime();
        this.updatedTime = question.getUpdatedTime();
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Question.QuestionType getType() {
        return type;
    }
    
    public void setType(Question.QuestionType type) {
        this.type = type;
    }
    
    public Question.Difficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(Question.Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
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
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @Override
    public String toString() {
        return "QuestionResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", difficulty=" + difficulty +
                ", status=" + status +
                ", createdTime=" + createdTime +
                '}';
    }
}