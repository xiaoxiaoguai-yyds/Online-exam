package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.entity.Exam;
import org.bishe.question.entity.ExamQuestion;
import org.bishe.question.entity.ExamRecord;
import org.bishe.question.dto.ExamQuestionDTO;
import org.bishe.question.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 考试管理控制器
 */
@RestController
@RequestMapping("/v1/exams")
@CrossOrigin(origins = "*")
public class ExamController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);
    
    @Autowired
    private ExamService examService;
    
    /**
     * 获取考试列表（分页）
     */
    @GetMapping
    public ApiResponse<Page<Exam>> getExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long createdBy) {
        logger.info("获取考试列表请求 - page: {}, size: {}, sortBy: {}, sortDir: {}, title: {}, status: {}, createdBy: {}", 
                   page, size, sortBy, sortDir, title, status, createdBy);
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Exam> exams;
            if (title != null && !title.trim().isEmpty()) {
                logger.info("按标题搜索考试: {}", title);
                exams = examService.searchExamsByTitle(title, pageable);
            } else if (status != null) {
                logger.info("按状态查询考试: {}", status);
                exams = examService.getExamsByStatus(status, pageable);
            } else if (createdBy != null) {
                logger.info("按创建者查询考试: {}", createdBy);
                exams = examService.getExamsByCreator(createdBy, pageable);
            } else {
                logger.info("查询所有考试");
                exams = examService.getAllExams(pageable);
            }
            
            logger.info("成功获取考试列表，总数: {}, 当前页数据: {}", exams.getTotalElements(), exams.getContent().size());
            return ApiResponse.success("获取考试列表成功", exams);
        } catch (Exception e) {
            logger.error("获取考试列表失败", e);
            return ApiResponse.error("获取考试列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取考试详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Exam> getExamById(@PathVariable Long id) {
        try {
            Exam exam = examService.getExamById(id);
            return ApiResponse.success("获取考试详情成功", exam);
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取考试详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建考试
     */
    @PostMapping
    public ApiResponse<Exam> createExam(@Valid @RequestBody Exam exam) {
        logger.info("创建考试请求 - 标题: {}, 开始时间: {}, 结束时间: {}, 时长: {}分钟", 
                   exam.getTitle(), exam.getStartTime(), exam.getEndTime(), exam.getDuration());
        try {
            Exam createdExam = examService.createExam(exam);
            logger.info("考试创建成功 - ID: {}, 标题: {}", createdExam.getId(), createdExam.getTitle());
            return ApiResponse.success("创建考试成功", createdExam);
        } catch (RuntimeException e) {
            logger.warn("创建考试失败 - 业务异常: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("创建考试失败 - 系统异常", e);
            return ApiResponse.error("创建考试失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新考试
     */
    @PutMapping("/{id}")
    public ApiResponse<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        logger.info("更新考试请求 - ID: {}, 标题: {}, 开始时间: {}, 结束时间: {}, 时长: {}分钟", 
                   id, exam.getTitle(), exam.getStartTime(), exam.getEndTime(), exam.getDuration());
        try {
            exam.setId(id);
            Exam updatedExam = examService.updateExam(exam);
            logger.info("考试更新成功 - ID: {}, 标题: {}", updatedExam.getId(), updatedExam.getTitle());
            return ApiResponse.success("更新考试成功", updatedExam);
        } catch (RuntimeException e) {
            logger.warn("更新考试失败 - 业务异常: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("更新考试失败 - 系统异常", e);
            return ApiResponse.error("更新考试失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除考试
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return ApiResponse.success("删除考试成功");
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除考试失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取考试的题目列表
     */
    @GetMapping("/{examId}/questions")
    public ApiResponse<List<ExamQuestionDTO>> getExamQuestions(@PathVariable Long examId) {
        try {
            List<ExamQuestionDTO> questions = examService.getExamQuestions(examId);
            return ApiResponse.success("获取考试题目成功", questions);
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取考试题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 为考试添加题目
     */
    @PostMapping("/{examId}/questions")
    public ApiResponse<String> addQuestionToExam(
            @PathVariable Long examId,
            @RequestBody Map<String, Object> request) {
        try {
            Long questionId = Long.valueOf(request.get("questionId").toString());
            Integer order = request.get("order") != null ? 
                Integer.valueOf(request.get("order").toString()) : null;
            Double score = request.get("score") != null ? 
                Double.valueOf(request.get("score").toString()) : null;
            
            examService.addQuestionToExam(examId, questionId, order, score);
            return ApiResponse.success("添加题目成功");
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("添加题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 从考试中移除题目
     */
    @DeleteMapping("/{examId}/questions/{questionId}")
    public ApiResponse<String> removeQuestionFromExam(
            @PathVariable Long examId,
            @PathVariable Long questionId) {
        try {
            examService.removeQuestionFromExam(examId, questionId);
            return ApiResponse.success("移除题目成功");
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("移除题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新考试题目分数
     */
    @PutMapping("/{examId}/questions/{questionId}/score")
    public ApiResponse<String> updateQuestionScore(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @RequestBody Map<String, Double> request) {
        try {
            Double score = request.get("score");
            examService.updateQuestionScore(examId, questionId, score);
            return ApiResponse.success("更新题目分数成功");
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新题目分数失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取考试统计信息
     */
    @GetMapping("/{examId}/statistics")
    public ApiResponse<Map<String, Object>> getExamStatistics(@PathVariable Long examId) {
        try {
            Map<String, Object> statistics = examService.getExamStatistics(examId);
            return ApiResponse.success("获取考试统计成功", statistics);
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取考试统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有考试记录列表（分页）
     */
    @GetMapping("/records")
    public ApiResponse<Page<ExamRecord>> getAllExamRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Integer status) {
        logger.info("获取所有考试记录请求 - page: {}, size: {}, sortBy: {}, sortDir: {}, examId: {}, studentName: {}, status: {}", 
                   page, size, sortBy, sortDir, examId, studentName, status);
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<ExamRecord> records = examService.getAllExamRecords(pageable, examId, studentName, status);
            
            logger.info("成功获取考试记录列表，总数: {}, 当前页数据: {}", records.getTotalElements(), records.getContent().size());
            return ApiResponse.success("获取考试记录列表成功", records);
        } catch (Exception e) {
            logger.error("获取考试记录列表失败", e);
            return ApiResponse.error("获取考试记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试记录列表
     */
    @GetMapping("/{examId}/records")
    public ApiResponse<List<ExamRecord>> getExamRecords(@PathVariable Long examId) {
        try {
            List<ExamRecord> records = examService.getExamRecords(examId);
            return ApiResponse.success("获取考试记录成功", records);
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取考试记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取正在进行的考试
     */
    @GetMapping("/active")
    public ApiResponse<List<Exam>> getActiveExams() {
        try {
            List<Exam> activeExams = examService.getActiveExams();
            return ApiResponse.success("获取正在进行的考试成功", activeExams);
        } catch (Exception e) {
            return ApiResponse.error("获取正在进行的考试失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取即将开始的考试
     */
    @GetMapping("/upcoming")
    public ApiResponse<List<Exam>> getUpcomingExams() {
        try {
            List<Exam> upcomingExams = examService.getUpcomingExams();
            return ApiResponse.success("获取即将开始的考试成功", upcomingExams);
        } catch (Exception e) {
            return ApiResponse.error("获取即将开始的考试失败: " + e.getMessage());
        }
    }
    
    /**
     * 编辑考试记录分数
     */
    @PutMapping("/records/{recordId}/score")
    public ApiResponse<String> updateExamRecordScore(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> request) {
        logger.info("编辑考试记录分数请求 - 记录ID: {}, 新分数: {}", recordId, request.get("score"));
        try {
            Object scoreObj = request.get("score");
            if (scoreObj == null) {
                return ApiResponse.badRequest("分数不能为空");
            }
            
            BigDecimal newScore;
            if (scoreObj instanceof Number) {
                newScore = BigDecimal.valueOf(((Number) scoreObj).doubleValue());
            } else {
                newScore = new BigDecimal(scoreObj.toString());
            }
            
            examService.updateExamRecordScore(recordId, newScore);
            logger.info("考试记录分数编辑成功 - 记录ID: {}, 新分数: {}", recordId, newScore);
            return ApiResponse.success("编辑考试记录分数成功");
        } catch (RuntimeException e) {
            logger.warn("编辑考试记录分数失败 - 业务异常: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("编辑考试记录分数失败 - 系统异常", e);
            return ApiResponse.error("编辑考试记录分数失败: " + e.getMessage());
        }
    }
    
    /**
     * 重新考试（重置考试状态）
     */
    @PostMapping("/records/{recordId}/reset")
    public ApiResponse<String> resetExamRecord(@PathVariable Long recordId) {
        logger.info("重新考试请求 - 记录ID: {}", recordId);
        try {
            examService.resetExamRecord(recordId);
            logger.info("考试记录重置成功 - 记录ID: {}", recordId);
            return ApiResponse.success("重新考试设置成功");
        } catch (RuntimeException e) {
            logger.warn("重新考试失败 - 业务异常: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("重新考试失败 - 系统异常", e);
            return ApiResponse.error("重新考试失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除考试记录
     */
    @DeleteMapping("/records/{recordId}")
    public ApiResponse<String> deleteExamRecord(@PathVariable Long recordId) {
        logger.info("删除考试记录请求 - 记录ID: {}", recordId);
        try {
            examService.deleteExamRecord(recordId);
            logger.info("考试记录删除成功 - 记录ID: {}", recordId);
            return ApiResponse.success("删除考试记录成功");
        } catch (RuntimeException e) {
            logger.warn("删除考试记录失败 - 业务异常: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("删除考试记录失败 - 系统异常", e);
            return ApiResponse.error("删除考试记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新考试状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateExamStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            examService.updateExamStatus(id, status);
            return ApiResponse.success("更新考试状态成功");
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新考试状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 提交考试
     */
    @PostMapping("/submit")
    public ApiResponse<String> submitExam(@RequestBody Map<String, Object> request) {
        try {
            Long examId = Long.valueOf(request.get("examId").toString());
            Long studentId = Long.valueOf(request.get("studentId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> answers = (Map<String, Object>) request.get("answers");
            String submitTime = request.get("submitTime").toString();
            
            examService.submitExam(examId, studentId, answers, submitTime);
            return ApiResponse.success("考试提交成功");
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("考试提交失败: " + e.getMessage());
        }
    }
}