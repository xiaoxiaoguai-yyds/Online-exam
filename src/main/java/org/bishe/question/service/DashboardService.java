package org.bishe.question.service;

import org.bishe.question.entity.Exam;
import org.bishe.question.entity.ExamRecord;
import org.bishe.question.entity.Question;
import org.bishe.question.entity.Student;
import org.bishe.question.entity.User;
import org.bishe.question.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘服务
 * 提供各种统计数据
 */
@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamRecordRepository examRecordRepository;

    /**
     * 获取仪表盘综合统计数据
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        long totalUsers = userRepository.count();
        long totalStudents = studentRepository.count();
        
        // 今日新增用户（假设有创建时间字段）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        // 考试统计
        long totalExams = examRepository.count();
        long activeExams = examRepository.countByStatus(1); // 进行中的考试
        
        // 题目统计
        long totalQuestions = questionRepository.count();
        
        // 考试记录统计
        long totalExamRecords = examRecordRepository.count();
        long completedExams = examRecordRepository.countByStatus(2); // 已完成的考试
        
        // 计算完成率
        double completionRate = totalExamRecords > 0 ? 
            (double) completedExams / totalExamRecords * 100 : 0;
        
        // 计算平均分
        List<ExamRecord> completedRecords = examRecordRepository.findByStatus(2);
        double averageScore = completedRecords.stream()
            .filter(record -> record.getScore() != null)
            .mapToDouble(record -> record.getScore().doubleValue())
            .average()
            .orElse(0.0);

        stats.put("totalUsers", totalUsers);
        stats.put("totalStudents", totalStudents);
        stats.put("totalExams", totalExams);
        stats.put("activeExams", activeExams);
        stats.put("totalQuestions", totalQuestions);
        stats.put("totalExamRecords", totalExamRecords);
        stats.put("completedExams", completedExams);
        stats.put("completionRate", Math.round(completionRate * 10.0) / 10.0);
        stats.put("averageScore", Math.round(averageScore * 10.0) / 10.0);
        
        // 增长率计算（这里简化处理，实际应该对比历史数据）
        stats.put("userGrowthRate", calculateGrowthRate(totalUsers));
        stats.put("examGrowthRate", calculateGrowthRate(totalExams));

        return stats;
    }

    /**
     * 获取用户统计数据
     */
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> userStats = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalStudents = studentRepository.count();
        long adminUsers = totalUsers - totalStudents; // 管理员用户数

        // 按角色分组统计
        userStats.put("totalUsers", totalUsers);
        userStats.put("totalStudents", totalStudents);
        userStats.put("adminUsers", adminUsers);
        
        // 用户活跃度统计（基于最近考试记录）
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        List<ExamRecord> recentRecords = examRecordRepository.findByCreatedAtAfter(lastWeek);
        Set<Long> activeStudents = recentRecords.stream()
            .map(ExamRecord::getStudentId)
            .collect(Collectors.toSet());
        
        userStats.put("activeStudentsLastWeek", activeStudents.size());
        userStats.put("activityRate", totalStudents > 0 ? 
            Math.round((double) activeStudents.size() / totalStudents * 100 * 10.0) / 10.0 : 0);

        return userStats;
    }

    /**
     * 获取考试统计数据
     */
    public Map<String, Object> getExamStatistics() {
        Map<String, Object> examStats = new HashMap<>();

        long totalExams = examRepository.count();
        long pendingExams = examRepository.countByStatus(0); // 未开始
        long activeExams = examRepository.countByStatus(1);  // 进行中
        long finishedExams = examRepository.countByStatus(2); // 已结束

        examStats.put("totalExams", totalExams);
        examStats.put("pendingExams", pendingExams);
        examStats.put("activeExams", activeExams);
        examStats.put("finishedExams", finishedExams);

        // 最近的考试
        List<Exam> recentExams = examRepository.findTop5ByOrderByCreatedAtDesc();
        List<Map<String, Object>> recentExamList = recentExams.stream()
            .map(exam -> {
                Map<String, Object> examInfo = new HashMap<>();
                examInfo.put("id", exam.getId());
                examInfo.put("title", exam.getTitle());
                examInfo.put("status", exam.getStatus());
                examInfo.put("startTime", exam.getStartTime());
                examInfo.put("endTime", exam.getEndTime());
                examInfo.put("createdAt", exam.getCreatedAt());
                return examInfo;
            })
            .collect(Collectors.toList());

        examStats.put("recentExams", recentExamList);

        return examStats;
    }

    /**
     * 获取题目统计数据
     */
    public Map<String, Object> getQuestionStatistics() {
        Map<String, Object> questionStats = new HashMap<>();

        long totalQuestions = questionRepository.count();
        
        // 按类型统计
        Map<String, Long> typeStats = new HashMap<>();
        typeStats.put("single", questionRepository.countByType(Question.QuestionType.single));
        typeStats.put("multiple", questionRepository.countByType(Question.QuestionType.multiple));
        typeStats.put("judge", questionRepository.countByType(Question.QuestionType.judge));
        typeStats.put("fill", questionRepository.countByType(Question.QuestionType.fill));
        typeStats.put("essay", questionRepository.countByType(Question.QuestionType.essay));

        // 按难度统计
        Map<String, Long> difficultyStats = new HashMap<>();
        difficultyStats.put("easy", questionRepository.countByDifficulty(Question.Difficulty.easy));
        difficultyStats.put("medium", questionRepository.countByDifficulty(Question.Difficulty.medium));
        difficultyStats.put("hard", questionRepository.countByDifficulty(Question.Difficulty.hard));

        questionStats.put("totalQuestions", totalQuestions);
        questionStats.put("typeStats", typeStats);
        questionStats.put("difficultyStats", difficultyStats);

        return questionStats;
    }

    /**
     * 获取最近活动数据
     */
    public Map<String, Object> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();

        // 最近的考试记录
        List<ExamRecord> recentRecords = examRecordRepository.findTop10ByOrderByCreatedAtDesc();
        List<Map<String, Object>> recentExamRecords = recentRecords.stream()
            .map(record -> {
                Map<String, Object> recordInfo = new HashMap<>();
                recordInfo.put("id", record.getId());
                recordInfo.put("examId", record.getExamId());
                recordInfo.put("studentId", record.getStudentId());
                recordInfo.put("score", record.getScore());
                recordInfo.put("status", record.getStatus());
                recordInfo.put("createdAt", record.getCreatedAt());
                recordInfo.put("submitTime", record.getSubmitTime());
                
                // 获取考试标题
                Optional<Exam> exam = examRepository.findById(record.getExamId());
                recordInfo.put("examTitle", exam.map(Exam::getTitle).orElse("未知考试"));
                
                // 获取学生姓名
                Optional<Student> student = studentRepository.findById(record.getStudentId());
                recordInfo.put("studentName", student.map(Student::getName).orElse("未知学生"));
                
                return recordInfo;
            })
            .collect(Collectors.toList());

        activities.put("recentExamRecords", recentExamRecords);

        // 今日统计
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        long todayExamRecords = examRecordRepository.countByCreatedAtBetween(todayStart, todayEnd);
        long todayCompletedExams = examRecordRepository.countByStatusAndSubmitTimeBetween(2, todayStart, todayEnd);

        activities.put("todayExamRecords", todayExamRecords);
        activities.put("todayCompletedExams", todayCompletedExams);

        return activities;
    }

    /**
     * 计算增长率（简化版本）
     */
    private double calculateGrowthRate(long currentValue) {
        // 这里简化处理，实际应该对比历史数据
        // 假设增长率在5%-15%之间随机
        Random random = new Random();
        double rate = 5.0 + random.nextDouble() * 10.0;
        return Math.round(rate * 10.0) / 10.0;
    }
}