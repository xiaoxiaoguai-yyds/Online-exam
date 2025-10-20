package org.bishe.question.service;

import org.bishe.question.dto.QuestionRequest;
import org.bishe.question.dto.QuestionResponse;
import org.bishe.question.entity.Question;
import org.bishe.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 题目服务类
 */
@Service
@Transactional
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建题目
     * @param questionRequest 题目请求
     * @param createdBy 创建者ID
     * @return 题目响应
     */
    public QuestionResponse createQuestion(QuestionRequest questionRequest, Long createdBy) {
        System.out.println("=== 创建题目开始 ===");
        System.out.println("题目标题: " + questionRequest.getTitle());
        System.out.println("题目类型: " + questionRequest.getType());
        System.out.println("难度等级: " + questionRequest.getDifficulty());
        
        // 检查题目标题是否已存在
        if (questionRepository.existsByTitle(questionRequest.getTitle())) {
            throw new RuntimeException("题目标题已存在");
        }
        
        // 创建题目实体
        Question question = new Question();
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setType(questionRequest.getType());
        question.setDifficulty(questionRequest.getDifficulty());
        question.setStatus(questionRequest.getStatus());
        question.setCreatedBy(createdBy);
        
        // 处理选项（选择题和判断题）
        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            try {
                question.setOptions(objectMapper.writeValueAsString(questionRequest.getOptions()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("选项数据格式错误", e);
            }
        }
        
        // 处理正确答案索引（选择题）
        if (questionRequest.getCorrectAnswers() != null && !questionRequest.getCorrectAnswers().isEmpty()) {
            try {
                question.setCorrectAnswers(objectMapper.writeValueAsString(questionRequest.getCorrectAnswers()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("正确答案数据格式错误", e);
            }
        }
        
        // 处理正确答案文本（填空题和问答题）
        if (StringUtils.hasText(questionRequest.getCorrectAnswer())) {
            question.setCorrectAnswer(questionRequest.getCorrectAnswer());
        }
        
        // 处理标签
        if (questionRequest.getTags() != null && !questionRequest.getTags().isEmpty()) {
            try {
                question.setTags(objectMapper.writeValueAsString(questionRequest.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("标签数据格式错误", e);
            }
        }
        
        // 保存题目
        Question savedQuestion = questionRepository.save(question);
        System.out.println("题目创建成功，ID: " + savedQuestion.getId());
        
        return convertToResponse(savedQuestion);
    }
    
    /**
     * 更新题目
     * @param id 题目ID
     * @param questionRequest 题目请求
     * @return 题目响应
     */
    public QuestionResponse updateQuestion(Long id, QuestionRequest questionRequest) {
        System.out.println("=== 更新题目开始 ===");
        System.out.println("题目ID: " + id);
        
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isEmpty()) {
            throw new RuntimeException("题目不存在");
        }
        
        Question question = questionOptional.get();
        
        // 检查题目标题是否已存在（排除当前题目）
        if (questionRepository.existsByTitleAndIdNot(questionRequest.getTitle(), id)) {
            throw new RuntimeException("题目标题已存在");
        }
        
        // 更新题目信息
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setType(questionRequest.getType());
        question.setDifficulty(questionRequest.getDifficulty());
        question.setStatus(questionRequest.getStatus());
        
        // 处理选项
        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            try {
                question.setOptions(objectMapper.writeValueAsString(questionRequest.getOptions()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("选项数据格式错误", e);
            }
        } else {
            question.setOptions(null);
        }
        
        // 处理正确答案索引
        if (questionRequest.getCorrectAnswers() != null && !questionRequest.getCorrectAnswers().isEmpty()) {
            try {
                question.setCorrectAnswers(objectMapper.writeValueAsString(questionRequest.getCorrectAnswers()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("正确答案数据格式错误", e);
            }
        } else {
            question.setCorrectAnswers(null);
        }
        
        // 处理正确答案文本
        question.setCorrectAnswer(questionRequest.getCorrectAnswer());
        
        // 处理标签
        if (questionRequest.getTags() != null && !questionRequest.getTags().isEmpty()) {
            try {
                question.setTags(objectMapper.writeValueAsString(questionRequest.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("标签数据格式错误", e);
            }
        } else {
            question.setTags(null);
        }
        
        Question updatedQuestion = questionRepository.save(question);
        System.out.println("题目更新成功");
        
        return convertToResponse(updatedQuestion);
    }
    
    /**
     * 直接保存题目实体（用于Excel导入）
     * @param question 题目实体
     * @return 保存后的题目实体
     */
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * 删除题目
     * @param id 题目ID
     */
    public void deleteQuestion(Long id) {
        System.out.println("=== 删除题目开始 ===");
        System.out.println("题目ID: " + id);
        
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("题目不存在");
        }
        
        questionRepository.deleteById(id);
        System.out.println("题目删除成功");
    }
    
    /**
     * 根据ID获取题目
     * @param id 题目ID
     * @return 题目响应
     */
    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long id) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isEmpty()) {
            throw new RuntimeException("题目不存在");
        }
        
        return convertToResponse(questionOptional.get());
    }
    
    /**
     * 根据ID获取题目实体（用于API接口）
     * @param id 题目ID
     * @return 题目实体
     */
    @Transactional(readOnly = true)
    public Question getQuestionEntityById(Long id) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        return questionOpt.orElse(null);
    }
    
    /**
     * 分页查询题目
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param type 题目类型
     * @param difficulty 难度等级
     * @return 题目分页响应
     */
    @Transactional(readOnly = true)
    public Page<QuestionResponse> getQuestions(int page, int size, String keyword, 
                                             Question.QuestionType type, Question.Difficulty difficulty) {
        System.out.println("=== 分页查询题目 ===");
        System.out.println("页码: " + page + ", 大小: " + size);
        System.out.println("关键词: " + keyword + ", 类型: " + type + ", 难度: " + difficulty);
        
        // 创建分页参数，按序号（ID）升序排列
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        
        Page<Question> questionPage;
        
        // 根据条件查询
        if (StringUtils.hasText(keyword) || type != null || difficulty != null) {
            questionPage = questionRepository.searchQuestions(keyword, type, difficulty, pageable);
        } else {
            questionPage = questionRepository.findByStatus(1, pageable);
        }
        
        // 转换为响应DTO
        return questionPage.map(this::convertToResponse);
    }
    
    /**
     * 获取题目实体列表（用于导出）
     * @param keyword 搜索关键词
     * @param type 题目类型
     * @param difficulty 难度等级
     * @return 题目实体列表
     */
    @Transactional(readOnly = true)
    public List<Question> getQuestionsForExport(String keyword, Question.QuestionType type, Question.Difficulty difficulty) {
        System.out.println("=== 获取题目用于导出 ===");
        System.out.println("关键词: " + keyword + ", 类型: " + type + ", 难度: " + difficulty);
        
        // 根据条件查询所有符合条件的题目
        if (StringUtils.hasText(keyword) || type != null || difficulty != null) {
            // 使用一个很大的分页大小来获取所有数据
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id"));
            Page<Question> questionPage = questionRepository.searchQuestions(keyword, type, difficulty, pageable);
            return questionPage.getContent();
        } else {
            // 获取所有启用状态的题目
            return questionRepository.findByStatusOrderByIdAsc(1);
        }
    }
    
    /**
     * 获取题目统计信息
     * @return 统计信息
     */
    @Transactional(readOnly = true)
    public Object getQuestionStats() {
        // 这里可以返回各种统计信息，如各类型题目数量、各难度题目数量等
        long totalCount = questionRepository.count();
        List<Object[]> typeStats = questionRepository.countByType();
        List<Object[]> difficultyStats = questionRepository.countByDifficulty();
        
        return new Object() {
            public final long total = totalCount;
            public final List<Object[]> byType = typeStats;
            public final List<Object[]> byDifficulty = difficultyStats;
        };
    }
    
    /**
     * 将Question实体转换为QuestionResponse
     * @param question 题目实体
     * @return 题目响应DTO
     */
    private QuestionResponse convertToResponse(Question question) {
        QuestionResponse response = new QuestionResponse(question);
        
        // 解析JSON字段
        try {
            if (StringUtils.hasText(question.getOptions())) {
                List<String> options = objectMapper.readValue(question.getOptions(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                response.setOptions(options);
            }
            
            if (StringUtils.hasText(question.getCorrectAnswers())) {
                try {
                    // 首先尝试解析为通用的List<Object>
                    List<Object> rawAnswers = objectMapper.readValue(question.getCorrectAnswers(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                    
                    // 将所有答案转换为字符串
                    List<String> correctAnswers = rawAnswers.stream()
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.toList());
                    
                    response.setCorrectAnswers(correctAnswers);
                } catch (Exception e) {
                    // 如果解析失败，尝试直接解析为字符串列表
                    List<String> correctAnswers = objectMapper.readValue(question.getCorrectAnswers(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    response.setCorrectAnswers(correctAnswers);
                }
            }
            
            if (StringUtils.hasText(question.getTags())) {
                List<String> tags = objectMapper.readValue(question.getTags(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                response.setTags(tags);
            }
        } catch (JsonProcessingException e) {
            System.err.println("JSON解析错误: " + e.getMessage());
        }
        
        return response;
    }
}