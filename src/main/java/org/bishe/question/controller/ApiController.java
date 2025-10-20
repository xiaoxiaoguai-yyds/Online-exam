package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.entity.Question;
import org.bishe.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 对外API接口控制器
 * 提供题目答案判断等功能
 */
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*") // 允许所有来源访问对外API
public class ApiController {
    
    @Autowired
    private QuestionService questionService;
    
    /**
     * 判断题目答案是否正确
     * @param request 答案判断请求
     * @return API响应
     */
    @PostMapping("/check-answer")
    public ApiResponse<AnswerCheckResult> checkAnswer(@Valid @RequestBody AnswerCheckRequest request) {
        System.out.println("=== 答案判断API调用 ===");
        System.out.println("题目ID: " + request.getQuestionId());
        System.out.println("用户答案: " + request.getUserAnswer());
        
        try {
            // 获取题目信息
            Question question = questionService.getQuestionEntityById(request.getQuestionId());
            if (question == null) {
                return ApiResponse.error("题目不存在");
            }
            
            // 判断答案是否正确
            boolean isCorrect = checkAnswerCorrectness(question, request.getUserAnswer());
            
            // 构建返回结果
            AnswerCheckResult result = new AnswerCheckResult();
            result.setQuestionId(request.getQuestionId());
            result.setCorrect(isCorrect);
            result.setQuestionType(question.getType().toString());
            result.setUserAnswer(request.getUserAnswer());
            
            // 如果答案错误，返回正确答案
            if (!isCorrect) {
                result.setCorrectAnswer(getCorrectAnswerString(question));
            }
            
            System.out.println("答案判断结果: " + (isCorrect ? "正确" : "错误"));
            return ApiResponse.success("答案判断完成", result);
            
        } catch (Exception e) {
            System.err.println("答案判断失败: " + e.getMessage());
            return ApiResponse.error("答案判断失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量判断题目答案
     * @param request 批量答案判断请求
     * @return API响应
     */
    @PostMapping("/check-answers")
    public ApiResponse<List<AnswerCheckResult>> checkAnswers(@Valid @RequestBody BatchAnswerCheckRequest request) {
        System.out.println("=== 批量答案判断API调用 ===");
        System.out.println("题目数量: " + request.getAnswers().size());
        
        try {
            List<AnswerCheckResult> results = request.getAnswers().stream()
                .map(answer -> {
                    try {
                        Question question = questionService.getQuestionEntityById(answer.getQuestionId());
                        if (question == null) {
                            AnswerCheckResult result = new AnswerCheckResult();
                            result.setQuestionId(answer.getQuestionId());
                            result.setCorrect(false);
                            result.setError("题目不存在");
                            return result;
                        }
                        
                        boolean isCorrect = checkAnswerCorrectness(question, answer.getUserAnswer());
                        
                        AnswerCheckResult result = new AnswerCheckResult();
                        result.setQuestionId(answer.getQuestionId());
                        result.setCorrect(isCorrect);
                        result.setQuestionType(question.getType().toString());
                        result.setUserAnswer(answer.getUserAnswer());
                        
                        if (!isCorrect) {
                            result.setCorrectAnswer(getCorrectAnswerString(question));
                        }
                        
                        return result;
                    } catch (Exception e) {
                        AnswerCheckResult result = new AnswerCheckResult();
                        result.setQuestionId(answer.getQuestionId());
                        result.setCorrect(false);
                        result.setError(e.getMessage());
                        return result;
                    }
                })
                .toList();
            
            System.out.println("批量答案判断完成");
            return ApiResponse.success("批量答案判断完成", results);
            
        } catch (Exception e) {
            System.err.println("批量答案判断失败: " + e.getMessage());
            return ApiResponse.error("批量答案判断失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取题目信息（不包含答案）
     * @param questionId 题目ID
     * @return API响应
     */
    @GetMapping("/question/{questionId}")
    public ApiResponse<QuestionInfo> getQuestionInfo(@PathVariable Long questionId) {
        System.out.println("=== 获取题目信息API调用 ===");
        System.out.println("题目ID: " + questionId);
        
        try {
            Question question = questionService.getQuestionEntityById(questionId);
            if (question == null) {
                return ApiResponse.error("题目不存在");
            }
            
            QuestionInfo info = new QuestionInfo();
            info.setId(question.getId());
            info.setTitle(question.getTitle());
            info.setContent(question.getContent());
            info.setType(question.getType().toString());
            info.setDifficulty(question.getDifficulty().toString());
            // 解析JSON字符串为List<String>
            try {
                if (question.getOptions() != null && !question.getOptions().trim().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<String> optionsList = mapper.readValue(question.getOptions(), new TypeReference<List<String>>(){});
                    info.setOptions(optionsList);
                } else {
                    info.setOptions(null);
                }
            } catch (Exception e) {
                info.setOptions(null);
            }
            
            System.out.println("获取题目信息成功");
            return ApiResponse.success("获取题目信息成功", info);
            
        } catch (Exception e) {
            System.err.println("获取题目信息失败: " + e.getMessage());
            return ApiResponse.error("获取题目信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 判断答案是否正确
     */
    private boolean checkAnswerCorrectness(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }
        
        String trimmedAnswer = userAnswer.trim();
        
        switch (question.getType()) {
            case single:
            case multiple:
                // 选择题：检查correctAnswers数组
                if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().trim().isEmpty()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> correctAnswersList = mapper.readValue(question.getCorrectAnswers(), new TypeReference<List<String>>(){});
                        return correctAnswersList.contains(trimmedAnswer);
                    } catch (Exception e) {
                        // 如果JSON解析失败，尝试直接比较
                        return question.getCorrectAnswers().trim().equals(trimmedAnswer);
                    }
                }
                break;
                
            case judge:
                // 判断题：优先检查correctAnswer字段，兼容correctAnswers数组
                if (question.getCorrectAnswer() != null && !question.getCorrectAnswer().trim().isEmpty()) {
                    return question.getCorrectAnswer().trim().equalsIgnoreCase(trimmedAnswer);
                }
                if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().trim().isEmpty()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> correctAnswersList = mapper.readValue(question.getCorrectAnswers(), new TypeReference<List<String>>(){});
                        return correctAnswersList.stream()
                            .anyMatch(answer -> answer.trim().equalsIgnoreCase(trimmedAnswer));
                    } catch (Exception e) {
                        return question.getCorrectAnswers().trim().equalsIgnoreCase(trimmedAnswer);
                    }
                }
                break;
                
            case fill:
            case essay:
                // 填空题和问答题：优先检查correctAnswer字段，兼容correctAnswers数组
                if (question.getCorrectAnswer() != null && !question.getCorrectAnswer().trim().isEmpty()) {
                    return question.getCorrectAnswer().trim().equalsIgnoreCase(trimmedAnswer);
                }
                if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().trim().isEmpty()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> correctAnswersList = mapper.readValue(question.getCorrectAnswers(), new TypeReference<List<String>>(){});
                        return correctAnswersList.stream()
                            .anyMatch(answer -> answer.trim().equalsIgnoreCase(trimmedAnswer));
                    } catch (Exception e) {
                        return question.getCorrectAnswers().trim().equalsIgnoreCase(trimmedAnswer);
                    }
                }
                break;
        }
        
        return false;
    }
    
    /**
     * 获取正确答案字符串
     */
    private String getCorrectAnswerString(Question question) {
        switch (question.getType()) {
            case single:
            case multiple:
                if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().trim().isEmpty()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> correctAnswersList = mapper.readValue(question.getCorrectAnswers(), new TypeReference<List<String>>(){});
                        return String.join(", ", correctAnswersList);
                    } catch (Exception e) {
                        return question.getCorrectAnswers();
                    }
                }
                break;
                
            case judge:
            case fill:
            case essay:
                if (question.getCorrectAnswer() != null && !question.getCorrectAnswer().trim().isEmpty()) {
                    return question.getCorrectAnswer();
                }
                if (question.getCorrectAnswers() != null && !question.getCorrectAnswers().trim().isEmpty()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> correctAnswersList = mapper.readValue(question.getCorrectAnswers(), new TypeReference<List<String>>(){});
                        return String.join(", ", correctAnswersList);
                    } catch (Exception e) {
                        return question.getCorrectAnswers();
                    }
                }
                break;
        }
        
        return "未设置正确答案";
    }
    
    // 内部类定义
    
    /**
     * 答案判断请求
     */
    public static class AnswerCheckRequest {
        @JsonProperty("questionId")
        private Long questionId;
        
        @JsonProperty("userAnswer")
        private String userAnswer;
        
        // 无参构造函数
        public AnswerCheckRequest() {}
        
        // 带参构造函数
        public AnswerCheckRequest(Long questionId, String userAnswer) {
            this.questionId = questionId;
            this.userAnswer = userAnswer;
        }
        
        // Getters and Setters
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getUserAnswer() { return userAnswer; }
        public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    }
    
    /**
     * 批量答案判断请求
     */
    public static class BatchAnswerCheckRequest {
        private List<AnswerCheckRequest> answers;
        
        // 无参构造函数
        public BatchAnswerCheckRequest() {}
        
        // 带参构造函数
        public BatchAnswerCheckRequest(List<AnswerCheckRequest> answers) {
            this.answers = answers;
        }
        
        // Getters and Setters
        public List<AnswerCheckRequest> getAnswers() { return answers; }
        public void setAnswers(List<AnswerCheckRequest> answers) { this.answers = answers; }
    }
    
    /**
     * 答案判断结果
     */
    public static class AnswerCheckResult {
        private Long questionId;
        private boolean correct;
        private String questionType;
        private String userAnswer;
        private String correctAnswer;
        private String error;
        
        // 无参构造函数
        public AnswerCheckResult() {}
        
        // Getters and Setters
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
        public String getQuestionType() { return questionType; }
        public void setQuestionType(String questionType) { this.questionType = questionType; }
        public String getUserAnswer() { return userAnswer; }
        public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    /**
     * 题目信息（不包含答案）
     */
    public static class QuestionInfo {
        private Long id;
        private String title;
        private String content;
        private String type;
        private String difficulty;
        private List<String> options;
        
        // 无参构造函数
        public QuestionInfo() {}
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }
}