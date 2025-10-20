package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.entity.Exam;
import org.bishe.question.entity.ExamRecord;
import org.bishe.question.entity.StudentAnswer;
import org.bishe.question.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/v1/student")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private ExamService examService;
    
    /**
     * 获取学生可参加的考试列表
     */
    @GetMapping("/exams/available")
    public ApiResponse<List<Exam>> getAvailableExams(@RequestParam Long studentId) {
        try {
            List<Exam> availableExams = examService.getAvailableExamsForStudent(studentId);
            return ApiResponse.success("获取可参加考试列表成功", availableExams);
        } catch (Exception e) {
            return ApiResponse.error("获取可参加考试列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学生已参加的考试列表
     */
    @GetMapping("/exams/participated")
    public ApiResponse<List<Exam>> getParticipatedExams(@RequestParam Long studentId) {
        try {
            List<Exam> participatedExams = examService.getExamsByStudent(studentId);
            return ApiResponse.success("获取已参加考试列表成功", participatedExams);
        } catch (Exception e) {
            return ApiResponse.error("获取已参加考试列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学生最近的考试记录
     */
    @GetMapping("/exam-records/recent")
    public ApiResponse<List<ExamRecord>> getRecentExamRecords(@RequestParam Long studentId, 
                                                             @RequestParam(defaultValue = "5") int limit) {
        try {
            List<ExamRecord> recentRecords = examService.getRecentExamRecordsByStudent(studentId, limit);
            return ApiResponse.success("获取最近考试记录成功", recentRecords);
        } catch (Exception e) {
            return ApiResponse.error("获取最近考试记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学生考试答案详情
     */
    @GetMapping("/exam-records/{examRecordId}/answers")
    public ApiResponse<List<StudentAnswer>> getExamAnswers(@PathVariable Long examRecordId, 
                                                          @RequestParam Long studentId) {
        try {
            List<StudentAnswer> answers = examService.getStudentAnswersByExamRecord(examRecordId, studentId);
            return ApiResponse.success("获取考试答案详情成功", answers);
        } catch (Exception e) {
            return ApiResponse.error("获取考试答案详情失败: " + e.getMessage());
        }
    }
}