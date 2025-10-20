package org.bishe.question.dto;

import org.bishe.question.entity.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 题目请求DTO
 */
public class QuestionRequest {
    
    @NotBlank(message = "题目标题不能为空")
    private String title;
    
    @NotBlank(message = "题目内容不能为空")
    private String content;
    
    @NotNull(message = "题目类型不能为空")
    private Question.QuestionType type;
    
    @NotNull(message = "难度等级不能为空")
    private Question.Difficulty difficulty;
    
    private List<String> options;
    
    private List<String> correctAnswers;
    
    private String correctAnswer;
    
    private List<String> tags;
    
    private Integer status = 1;
    
    // 构造函数
    public QuestionRequest() {}
    
    public QuestionRequest(String title, String content, Question.QuestionType type, Question.Difficulty difficulty) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.difficulty = difficulty;
    }
    
    // Getter和Setter方法
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
    
    @Override
    public String toString() {
        return "QuestionRequest{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", difficulty=" + difficulty +
                ", status=" + status +
                '}';
    }
}