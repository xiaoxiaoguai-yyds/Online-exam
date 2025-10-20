package org.bishe.question.service;

import org.bishe.question.entity.Exam;
import org.bishe.question.entity.ExamQuestion;
import org.bishe.question.entity.ExamRecord;
import org.bishe.question.entity.Question;
import org.bishe.question.entity.Student;
import org.bishe.question.entity.StudentAnswer;
import org.bishe.question.dto.ExamQuestionDTO;
import org.bishe.question.repository.ExamRepository;
import org.bishe.question.repository.ExamQuestionRepository;
import org.bishe.question.repository.ExamRecordRepository;
import org.bishe.question.repository.QuestionRepository;
import org.bishe.question.repository.StudentRepository;
import org.bishe.question.repository.StudentAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考试服务类
 */
@Service
@Transactional
public class ExamService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamService.class);
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    
    @Autowired
    private ExamRecordRepository examRecordRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentAnswerRepository studentAnswerRepository;
    
    /**
     * 创建考试
     */
    public Exam createExam(Exam exam) {
        logger.info("=== 开始创建考试 ===");
        logger.info("考试标题: {}", exam.getTitle());
        logger.info("考试描述: {}", exam.getDescription());
        logger.info("开始时间: {}", exam.getStartTime());
        logger.info("结束时间: {}", exam.getEndTime());
        logger.info("考试时长: {} 分钟", exam.getDuration());
        logger.info("总分: {}", exam.getTotalScore());
        logger.info("及格分: {}", exam.getPassScore());
        
        try {
            // 验证考试时间
            logger.info("验证考试时间...");
            validateExamTime(exam);
            logger.info("考试时间验证通过");
            
            // 检查标题是否重复
            logger.info("检查考试标题是否重复: {}", exam.getTitle());
            boolean titleExists = examRepository.existsByTitle(exam.getTitle());
            logger.info("标题重复检查结果: {}", titleExists ? "标题已存在" : "标题可用");
            
            if (titleExists) {
                logger.error("考试创建失败: 标题已存在 - {}", exam.getTitle());
                throw new RuntimeException("考试标题已存在");
            }
            
            logger.info("保存考试到数据库...");
            Exam savedExam = examRepository.save(exam);
            logger.info("考试创建成功，ID: {}, 标题: {}", savedExam.getId(), savedExam.getTitle());
            
            return savedExam;
        } catch (Exception e) {
            logger.error("创建考试失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 更新考试
     */
    public Exam updateExam(Exam exam) {
        Optional<Exam> existingExamOpt = examRepository.findById(exam.getId());
        if (existingExamOpt.isEmpty()) {
            throw new RuntimeException("考试不存在");
        }
        
        // 检查标题是否重复（排除当前考试）
        if (examRepository.existsByTitleAndIdNot(exam.getTitle(), exam.getId())) {
            throw new RuntimeException("考试标题已存在");
        }
        
        // 验证考试时间（更新时使用宽松的验证）
        validateExamTimeForUpdate(exam);
        
        exam.setUpdatedAt(LocalDateTime.now());
        
        return examRepository.save(exam);
    }
    
    /**
     * 删除考试
     */
    public void deleteExam(Long id) {
        Exam exam = getExamById(id);
        
        // 检查是否有学生已参加考试
        Long recordCount = examRecordRepository.countByExamId(id);
        if (recordCount > 0) {
            throw new RuntimeException("已有学生参加考试，无法删除");
        }
        
        // 删除考试题目关联
        examQuestionRepository.deleteByExamId(id);
        
        // 删除考试
        examRepository.delete(exam);
    }
    
    /**
     * 根据ID获取考试
     */
    @Transactional(readOnly = true)
    public Exam getExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
    }
    
    /**
     * 获取所有考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    
    /**
     * 获取所有考试（分页）
     */
    @Transactional(readOnly = true)
    public Page<Exam> getAllExams(Pageable pageable) {
        logger.info("=== 分页查询所有考试 ===");
        logger.info("页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Exam> examPage = examRepository.findAll(pageable);
            logger.info("查询成功，总数: {}, 当前页数量: {}, 总页数: {}", 
                examPage.getTotalElements(), examPage.getNumberOfElements(), examPage.getTotalPages());
            
            return examPage;
        } catch (Exception e) {
            logger.error("分页查询所有考试失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 根据状态获取考试（分页）
     */
    @Transactional(readOnly = true)
    public Page<Exam> getExamsByStatus(Integer status, Pageable pageable) {
        logger.info("=== 根据状态分页查询考试 ===");
        logger.info("状态: {}, 页码: {}, 大小: {}", status, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Exam> examPage = examRepository.findByStatus(status, pageable);
            logger.info("状态查询成功，总数: {}, 当前页数量: {}, 总页数: {}", 
                examPage.getTotalElements(), examPage.getNumberOfElements(), examPage.getTotalPages());
            
            return examPage;
        } catch (Exception e) {
            logger.error("根据状态分页查询考试失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 根据创建者获取考试（分页）
     */
    @Transactional(readOnly = true)
    public Page<Exam> getExamsByCreator(Long createdBy, Pageable pageable) {
        return examRepository.findByCreatedBy(createdBy, pageable);
    }
    
    /**
     * 根据标题搜索考试（分页）
     */
    @Transactional(readOnly = true)
    public Page<Exam> searchExamsByTitle(String title, Pageable pageable) {
        logger.info("=== 根据标题搜索考试 ===");
        logger.info("搜索关键词: {}, 页码: {}, 大小: {}", title, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Exam> examPage = examRepository.findByTitleContaining(title, pageable);
            logger.info("标题搜索成功，总数: {}, 当前页数量: {}, 总页数: {}", 
                examPage.getTotalElements(), examPage.getNumberOfElements(), examPage.getTotalPages());
            
            return examPage;
        } catch (Exception e) {
            logger.error("根据标题搜索考试失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 根据状态获取考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getExamsByStatus(Integer status) {
        return examRepository.findByStatus(status);
    }
    
    /**
     * 根据创建者获取考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getExamsByCreator(Long createdBy) {
        return examRepository.findByCreatedBy(createdBy);
    }
    
    /**
     * 搜索考试
     */
    @Transactional(readOnly = true)
    public List<Exam> searchExams(String title) {
        return examRepository.findByTitleContaining(title);
    }
    
    /**
     * 获取正在进行的考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getActiveExams() {
        return examRepository.findActiveExams(LocalDateTime.now());
    }
    
    /**
     * 获取即将开始的考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getUpcomingExams() {
        return examRepository.findUpcomingExams(LocalDateTime.now());
    }
    
    /**
     * 获取已结束的考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getFinishedExams() {
        return examRepository.findFinishedExams(LocalDateTime.now());
    }
    
    /**
     * 获取学生可参加的考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getAvailableExamsForStudent(Long studentId) {
        logger.info("=== 查询学生可用考试 ===");
        logger.info("学生ID: {}", studentId);
        LocalDateTime now = LocalDateTime.now();
        logger.info("当前时间: {}", now);
        
        // 先查询所有启用的考试
        List<Exam> allActiveExams = examRepository.findByStatus(1);
        logger.info("所有启用状态的考试数量: {}", allActiveExams.size());
        
        for (Exam exam : allActiveExams) {
            logger.info("考试详情: ID={}, 标题={}, 开始时间={}, 结束时间={}, 当前时间={}", 
                exam.getId(), exam.getTitle(), exam.getStartTime(), exam.getEndTime(), now);
            
            // 检查学生是否已参加过此考试
            boolean hasRecord = examRecordRepository.existsByExamIdAndStudentId(exam.getId(), studentId);
            logger.info("学生是否已参加考试{}: {}", exam.getId(), hasRecord);
        }
        
        List<Exam> availableExams = examRepository.findAvailableExamsForStudent(studentId, now);
        logger.info("查询到的可用考试数量: {}", availableExams.size());
        
        if (availableExams.isEmpty()) {
            logger.warn("没有找到可用考试，检查以下条件:");
            logger.warn("1. 考试状态是否为1（启用）");
            logger.warn("2. 考试结束时间是否大于当前时间: {}", now);
            logger.warn("3. 学生是否已参加过考试（exam_records表）");
        } else {
            for (Exam exam : availableExams) {
                logger.info("可用考试: ID={}, 标题={}, 状态={}, 开始时间={}, 结束时间={}", 
                    exam.getId(), exam.getTitle(), exam.getStatus(), exam.getStartTime(), exam.getEndTime());
            }
        }
        
        return availableExams;
    }
    
    /**
     * 获取学生已参加的考试
     */
    @Transactional(readOnly = true)
    public List<Exam> getExamsByStudent(Long studentId) {
        return examRepository.findExamsByStudent(studentId);
    }
    
    /**
     * 获取学生最近的考试记录
     */
    @Transactional(readOnly = true)
    public List<ExamRecord> getRecentExamRecordsByStudent(Long studentId, int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            return examRecordRepository.findRecentExamRecordsByStudentId(studentId, pageable);
        } catch (Exception e) {
            logger.error("获取学生最近考试记录失败: studentId={}, limit={}, error={}", studentId, limit, e.getMessage());
            throw new RuntimeException("获取学生最近考试记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 为考试添加题目
     */
    public void addQuestionToExam(Long examId, Long questionId, Integer order, Double score) {
        // 检查考试是否存在
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isEmpty()) {
            throw new RuntimeException("考试不存在");
        }
        
        // 检查题目是否存在
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("题目不存在");
        }
        
        // 检查是否已经添加过该题目
        if (examQuestionRepository.existsByExamIdAndQuestionId(examId, questionId)) {
            throw new RuntimeException("该题目已经添加到考试中");
        }
        
        // 创建考试题目关联
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExamId(examId);
        examQuestion.setQuestionId(questionId);
        if (order != null) {
            examQuestion.setQuestionOrder(order);
        }
        if (score != null) {
            examQuestion.setQuestionScore(BigDecimal.valueOf(score));
        }
        examQuestionRepository.save(examQuestion);
    }
    
    /**
     * 从考试中移除题目
     */
    public void removeQuestionFromExam(Long examId, Long questionId) {
        Exam exam = getExamById(examId);
        
        // 检查考试是否已开始
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("考试已开始，无法移除题目");
        }
        
        // 获取题目信息
        ExamQuestion examQuestion = examQuestionRepository.findByExamIdAndQuestionId(examId, questionId)
                .orElseThrow(() -> new RuntimeException("题目不在考试中"));
        
        // 删除题目
        examQuestionRepository.delete(examQuestion);
        
        // 调整其他题目的顺序
        examQuestionRepository.adjustQuestionOrderAfterDelete(examId, examQuestion.getQuestionOrder());
        
        // 更新考试统计信息
        updateExamStatistics(examId);
    }
    
    /**
     * 更新题目分数
     */
    public void updateQuestionScore(Long examId, Long questionId, Double score) {
        // 检查考试题目关联是否存在
        Optional<ExamQuestion> examQuestionOpt = examQuestionRepository.findByExamIdAndQuestionId(examId, questionId);
        if (examQuestionOpt.isEmpty()) {
            throw new RuntimeException("考试题目关联不存在");
        }
        
        if (score == null || score <= 0) {
            throw new RuntimeException("题目分数必须大于0");
        }
        
        examQuestionRepository.updateQuestionScore(examId, questionId, BigDecimal.valueOf(score));
    }
    
    /**
     * 获取考试题目列表
     */
    @Transactional(readOnly = true)
    public List<ExamQuestionDTO> getExamQuestions(Long examId) {
        logger.info("获取考试题目列表 - examId: {}", examId);
        
        // 检查考试是否存在
        if (!examRepository.existsById(examId)) {
            throw new RuntimeException("考试不存在");
        }
        
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionOrder(examId);
        List<ExamQuestionDTO> result = new ArrayList<>();
        
        // 转换为DTO并加载题目信息
        for (ExamQuestion examQuestion : examQuestions) {
            ExamQuestionDTO dto = new ExamQuestionDTO();
            dto.setId(examQuestion.getId());
            dto.setExamId(examQuestion.getExamId());
            dto.setQuestionId(examQuestion.getQuestionId());
            dto.setQuestionOrder(examQuestion.getQuestionOrder());
            dto.setScore(examQuestion.getScore());
            dto.setCreatedAt(examQuestion.getCreatedAt());
            
            // 加载题目详细信息
            if (examQuestion.getQuestionId() != null) {
                Optional<Question> questionOpt = questionRepository.findById(examQuestion.getQuestionId());
                if (questionOpt.isPresent()) {
                    Question question = questionOpt.get();
                    dto.setQuestionTitle(question.getTitle());
                    dto.setQuestionContent(question.getContent());
                    dto.setQuestionType(question.getType().name());
                    dto.setQuestionDifficulty(question.getDifficulty().name());
                    dto.setQuestionOptions(question.getOptions());
                    dto.setQuestionCorrectAnswers(question.getCorrectAnswers());
                    dto.setQuestionCorrectAnswer(question.getCorrectAnswer());
                    dto.setQuestionTags(question.getTags());
                }
            }
            
            result.add(dto);
        }
        
        logger.info("成功获取考试题目列表，数量: {}", result.size());
        return result;
    }
    
    /**
     * 更新考试状态
     */
    public void updateExamStatus(Long id, Integer status) {
        Optional<Exam> examOpt = examRepository.findById(id);
        if (examOpt.isEmpty()) {
            throw new RuntimeException("考试不存在");
        }
        
        Exam exam = examOpt.get();
        exam.setStatus(status);
        exam.setUpdatedAt(LocalDateTime.now());
        examRepository.save(exam);
        
        // 如果是结束考试，自动提交所有未提交的考试记录
        if (status == 2) {
            List<ExamRecord> unsubmittedRecords = examRecordRepository.findByExamIdAndStatus(id, 1);
            LocalDateTime now = LocalDateTime.now();
            for (ExamRecord record : unsubmittedRecords) {
                record.setStatus(2); // 设置为已提交
                record.setSubmitTime(now);
                // 计算实际用时
                if (record.getStartTime() != null) {
                    long minutes = java.time.Duration.between(record.getStartTime(), now).toMinutes();
                    record.setDurationMinutes((int) minutes);
                }
                examRecordRepository.save(record);
            }
        }
    }
    
    /**
     * 批量更新过期考试状态
     */
    public void updateExpiredExams() {
        examRepository.updateExpiredExamsStatus(LocalDateTime.now(), LocalDateTime.now());
        
        // 同时处理超时的考试记录
        examRecordRepository.timeoutInProgressRecords(LocalDateTime.now(), LocalDateTime.now());
    }
    
    /**
     * 获取考试统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getExamStatistics(Long examId) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isEmpty()) {
            throw new RuntimeException("考试不存在");
        }
        
        Exam exam = examOpt.get();
        Long totalParticipants = examRecordRepository.countByExamId(examId);
        Long completedCount = examRecordRepository.countByExamIdAndStatus(examId, 3);
        Long passedCount = examRecordRepository.countByExamIdAndStatusAndTotalScoreGreaterThanEqual(examId, 3, exam.getPassScore());
        Long failedCount = completedCount - passedCount;
        BigDecimal averageScore = examRecordRepository.getAverageScoreByExamIdAndStatus(examId, 3);
        BigDecimal maxScore = examRecordRepository.getMaxScoreByExamIdAndStatus(examId, 3);
        BigDecimal minScore = examRecordRepository.getMinScoreByExamIdAndStatus(examId, 3);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("exam", exam);
        statistics.put("totalParticipants", totalParticipants);
        statistics.put("completedCount", completedCount);
        statistics.put("passedCount", passedCount);
        statistics.put("failedCount", failedCount);
        statistics.put("averageScore", averageScore);
        statistics.put("maxScore", maxScore);
        statistics.put("minScore", minScore);
        statistics.put("completionRate", totalParticipants > 0 ? (double) completedCount / totalParticipants * 100 : 0);
        statistics.put("passRate", completedCount > 0 ? (double) passedCount / completedCount * 100 : 0);
        
        return statistics;
    }
    
    /**
     * 获取所有考试记录列表（分页）
     */
    @Transactional(readOnly = true)
    public Page<ExamRecord> getAllExamRecords(Pageable pageable, Long examId, String studentName, Integer status) {
        logger.info("=== 分页查询所有考试记录 ===");
        logger.info("页码: {}, 大小: {}, 考试ID: {}, 学生姓名: {}, 状态: {}", 
                   pageable.getPageNumber(), pageable.getPageSize(), examId, studentName, status);
        
        try {
            Page<ExamRecord> recordPage;
            
            if (examId != null && studentName != null && !studentName.trim().isEmpty() && status != null) {
                // 三个条件都有
                recordPage = examRecordRepository.findByExamIdAndStudentNameContainingAndStatus(examId, studentName, status, pageable);
            } else if (examId != null && studentName != null && !studentName.trim().isEmpty()) {
                // 考试ID和学生姓名
                recordPage = examRecordRepository.findByExamIdAndStudentNameContaining(examId, studentName, pageable);
            } else if (examId != null && status != null) {
                // 考试ID和状态
                recordPage = examRecordRepository.findByExamIdAndStatus(examId, status, pageable);
            } else if (studentName != null && !studentName.trim().isEmpty() && status != null) {
                // 学生姓名和状态
                recordPage = examRecordRepository.findByStudentNameContainingAndStatus(studentName, status, pageable);
            } else if (examId != null) {
                // 只有考试ID
                recordPage = examRecordRepository.findByExamId(examId, pageable);
            } else if (studentName != null && !studentName.trim().isEmpty()) {
                // 只有学生姓名
                recordPage = examRecordRepository.findByStudentNameContaining(studentName, pageable);
            } else if (status != null) {
                // 只有状态
                recordPage = examRecordRepository.findByStatus(status, pageable);
            } else {
                // 查询所有
                recordPage = examRecordRepository.findAll(pageable);
            }
            
            logger.info("查询成功，总数: {}, 当前页数量: {}, 总页数: {}", 
                recordPage.getTotalElements(), recordPage.getNumberOfElements(), recordPage.getTotalPages());
            
            return recordPage;
        } catch (Exception e) {
            logger.error("分页查询所有考试记录失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取考试记录列表
     */
    @Transactional(readOnly = true)
    public List<ExamRecord> getExamRecords(Long examId) {
        return examRecordRepository.findByExamId(examId);
    }
    
    /**
     * 提交考试
     */
    public void submitExam(Long examId, Long studentId, Map<String, Object> answers, String submitTime) {
        // 检查考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 检查考试是否正在进行中
        if (!exam.isInProgress()) {
            throw new RuntimeException("考试未开始或已结束");
        }
        
        // 查找或创建考试记录
        ExamRecord examRecord = examRecordRepository.findByExamIdAndStudentId(examId, studentId)
                .orElse(null);
        
        if (examRecord == null) {
            // 获取学生信息
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生不存在"));
            
            // 创建新的考试记录
            examRecord = new ExamRecord();
            examRecord.setExamId(examId);
            examRecord.setStudentId(studentId);
            examRecord.setStudentNumber(student.getStudentNumber());
            examRecord.setStudentName(student.getName());
            examRecord.setStartTime(LocalDateTime.now().minusMinutes(exam.getDuration()));
            examRecord.setStatus(1); // 进行中
        }
        
        // 更新提交信息
        examRecord.setEndTime(LocalDateTime.now());
        examRecord.setSubmitTime(LocalDateTime.now());
        examRecord.setStatus(2); // 已提交
        
        // 计算考试用时
        if (examRecord.getStartTime() != null) {
            long minutes = java.time.Duration.between(examRecord.getStartTime(), examRecord.getEndTime()).toMinutes();
            examRecord.setDurationMinutes((int) minutes);
        }
        
        examRecord = examRecordRepository.save(examRecord);
        
        // 保存学生答案
        for (Map.Entry<String, Object> entry : answers.entrySet()) {
            Long questionId = Long.valueOf(entry.getKey());
            String answer = entry.getValue() != null ? entry.getValue().toString() : "";
            
            // 查找或创建学生答案记录
            StudentAnswer studentAnswer = studentAnswerRepository
                    .findByExamRecordIdAndQuestionId(examRecord.getId(), questionId)
                    .orElse(new StudentAnswer());
            
            studentAnswer.setExamRecordId(examRecord.getId());
            studentAnswer.setExamId(examId);
            studentAnswer.setStudentId(studentId);
            studentAnswer.setQuestionId(questionId);
            studentAnswer.setStudentAnswer(answer);
            studentAnswer.setAnswerTime(LocalDateTime.now());
            
            // 获取题目信息并设置正确答案
            Question question = questionRepository.findById(questionId).orElse(null);
            if (question != null) {
                // 设置Question关联对象，用于答案比对
                studentAnswer.setQuestion(question);
                
                // 根据题目类型设置正确答案
                if (question.getType() == Question.QuestionType.single || 
                    question.getType() == Question.QuestionType.multiple) {
                    // 选择题使用correctAnswers字段
                    studentAnswer.setCorrectAnswer(question.getCorrectAnswers());
                } else {
                    // 填空题、判断题、问答题使用correctAnswer字段
                    studentAnswer.setCorrectAnswer(question.getCorrectAnswer());
                }
                
                // 检查答案是否正确
                studentAnswer.checkAnswer();
            }
            
            studentAnswerRepository.save(studentAnswer);
        }
        
        // 计算总分
        calculateExamScore(examRecord.getId());
    }
    
    /**
     * 编辑考试记录分数
     */
    public void updateExamRecordScore(Long recordId, BigDecimal newScore) {
        ExamRecord examRecord = examRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("考试记录不存在"));
        
        // 检查考试记录是否已完成
        if (examRecord.getStatus() != 2 && examRecord.getStatus() != 3) {
            throw new RuntimeException("只能编辑已完成的考试记录分数");
        }
        
        examRecord.setScore(newScore);
        examRecord.setUpdatedAt(LocalDateTime.now());
        examRecordRepository.save(examRecord);
        
        logger.info("考试记录分数已更新 - 记录ID: {}, 新分数: {}", recordId, newScore);
    }
    
    /**
     * 重新考试（重置考试状态）
     */
    public void resetExamRecord(Long recordId) {
        ExamRecord examRecord = examRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("考试记录不存在"));
        
        // 重置考试记录状态
        examRecord.setStatus(0); // 未开始
        examRecord.setStartTime(null);
        examRecord.setEndTime(null);
        examRecord.setSubmitTime(null);
        examRecord.setScore(BigDecimal.ZERO);
        examRecord.setCorrectCount(0);
        examRecord.setWrongCount(0);
        examRecord.setUnansweredCount(0);
        examRecord.setUpdatedAt(LocalDateTime.now());
        
        // 删除相关的学生答案记录
        studentAnswerRepository.deleteByExamRecordId(recordId);
        
        examRecordRepository.save(examRecord);
        
        logger.info("考试记录已重置 - 记录ID: {}, 学生ID: {}", recordId, examRecord.getStudentId());
    }
    
    /**
     * 删除考试记录
     */
    public void deleteExamRecord(Long recordId) {
        ExamRecord examRecord = examRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("考试记录不存在"));
        
        // 删除相关的学生答案记录
        studentAnswerRepository.deleteByExamRecordId(recordId);
        
        // 删除考试记录
        examRecordRepository.deleteById(recordId);
        
        logger.info("考试记录已删除 - 记录ID: {}, 学生ID: {}", recordId, examRecord.getStudentId());
    }

    /**
     * 计算考试分数
     */
    private void calculateExamScore(Long examRecordId) {
        ExamRecord examRecord = examRecordRepository.findById(examRecordId)
                .orElseThrow(() -> new RuntimeException("考试记录不存在"));
        
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByExamRecordId(examRecordId);
        
        int correctCount = 0;
        int wrongCount = 0;
        int unansweredCount = 0;
        BigDecimal totalScore = BigDecimal.ZERO;
        
        for (StudentAnswer studentAnswer : studentAnswers) {
            if (studentAnswer.getStudentAnswer() == null || studentAnswer.getStudentAnswer().trim().isEmpty()) {
                unansweredCount++;
            } else if (Boolean.TRUE.equals(studentAnswer.getIsCorrect())) {
                correctCount++;
                // 获取题目分数
                ExamQuestion examQuestion = examQuestionRepository
                        .findByExamIdAndQuestionId(examRecord.getExamId(), studentAnswer.getQuestionId())
                        .orElse(null);
                if (examQuestion != null && examQuestion.getScore() != null) {
                    totalScore = totalScore.add(examQuestion.getScore());
                }
            } else {
                wrongCount++;
            }
        }
        
        // 更新考试记录
        examRecord.setScore(totalScore);
        examRecord.setCorrectCount(correctCount);
        examRecord.setWrongCount(wrongCount);
        examRecord.setUnansweredCount(unansweredCount);
        examRecord.setUpdatedAt(LocalDateTime.now());
        
        examRecordRepository.save(examRecord);
    }
    
    // 私有辅助方法
    
    private void validateExamTime(Exam exam) {
        if (exam.getStartTime().isAfter(exam.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }
        
        // 开始时间不能早于当前时间（允许1分钟的误差）
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime().isBefore(now.minusMinutes(1))) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }
        
        long totalMinutes = java.time.Duration.between(exam.getStartTime(), exam.getEndTime()).toMinutes();
        if (totalMinutes < exam.getDuration()) {
            throw new RuntimeException("考试时长不能超过开始和结束时间的间隔");
        }
    }
    
    /**
     * 验证更新考试的时间（更宽松的验证规则）
     */
    private void validateExamTimeForUpdate(Exam exam) {
        if (exam.getStartTime().isAfter(exam.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }
        
        long totalMinutes = java.time.Duration.between(exam.getStartTime(), exam.getEndTime()).toMinutes();
        if (totalMinutes < exam.getDuration()) {
            throw new RuntimeException("考试时长不能超过开始和结束时间的间隔");
        }
        
        // 更新考试时不验证开始时间是否早于当前时间，允许管理员修改已开始的考试
    }
    

    
    private void updateExamStatistics(Long examId) {
        // 更新题目数量
        Long questionCount = examQuestionRepository.countByExamId(examId);
        
        // 计算总分
        BigDecimal totalScore = examQuestionRepository.calculateTotalScoreByExamId(examId);
        if (totalScore == null) {
            totalScore = BigDecimal.ZERO;
        }
        
        // 更新考试信息
        Exam exam = getExamById(examId);
        exam.setQuestionCount(questionCount.intValue());
        exam.setTotalScore(totalScore);
        examRepository.save(exam);
    }
    
    /**
     * 考试统计信息内部类
     */
    public static class ExamStatistics {
        private final Exam exam;
        private final Long totalParticipants;
        private final Long completedCount;
        private final Long passedCount;
        private final Long failedCount;
        private final BigDecimal averageScore;
        private final BigDecimal maxScore;
        private final BigDecimal minScore;
        
        public ExamStatistics(Exam exam, Long totalParticipants, Long completedCount,
                            Long passedCount, Long failedCount, BigDecimal averageScore,
                            BigDecimal maxScore, BigDecimal minScore) {
            this.exam = exam;
            this.totalParticipants = totalParticipants;
            this.completedCount = completedCount;
            this.passedCount = passedCount;
            this.failedCount = failedCount;
            this.averageScore = averageScore;
            this.maxScore = maxScore;
            this.minScore = minScore;
        }
        
        // Getters
        public Exam getExam() { return exam; }
        public Long getTotalParticipants() { return totalParticipants; }
        public Long getCompletedCount() { return completedCount; }
        public Long getPassedCount() { return passedCount; }
        public Long getFailedCount() { return failedCount; }
        public BigDecimal getAverageScore() { return averageScore; }
        public BigDecimal getMaxScore() { return maxScore; }
        public BigDecimal getMinScore() { return minScore; }
        
        public double getCompletionRate() {
            return totalParticipants > 0 ? (double) completedCount / totalParticipants * 100 : 0;
        }
        
        public double getPassRate() {
            return completedCount > 0 ? (double) passedCount / completedCount * 100 : 0;
        }
    }
    
    /**
     * 获取学生考试答案详情
     */
    @Transactional(readOnly = true)
    public List<StudentAnswer> getStudentAnswersByExamRecord(Long examRecordId, Long studentId) {
        // 验证考试记录是否存在且属于该学生
        ExamRecord examRecord = examRecordRepository.findById(examRecordId)
                .orElseThrow(() -> new RuntimeException("考试记录不存在"));
        
        if (!examRecord.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权限查看该考试记录");
        }
        
        // 获取学生答案详情，按题目ID排序
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByExamRecordIdOrderByQuestionId(examRecordId);
        
        // 加载关联的题目信息
        for (StudentAnswer answer : studentAnswers) {
            if (answer.getQuestionId() != null) {
                Optional<Question> question = questionRepository.findById(answer.getQuestionId());
                question.ifPresent(answer::setQuestion);
            }
        }
        
        return studentAnswers;
    }
}