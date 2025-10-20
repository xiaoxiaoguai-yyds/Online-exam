package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 仪表盘控制器
 * 提供仪表盘统计数据API接口
 */
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = dashboardService.getDashboardStatistics();
            return ApiResponse.success("获取仪表盘统计数据成功", stats);
        } catch (Exception e) {
            return ApiResponse.error("获取仪表盘统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/user-stats")
    public ApiResponse<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> userStats = dashboardService.getUserStatistics();
            return ApiResponse.success("获取用户统计数据成功", userStats);
        } catch (Exception e) {
            return ApiResponse.error("获取用户统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取考试统计数据
     */
    @GetMapping("/exam-stats")
    public ApiResponse<Map<String, Object>> getExamStats() {
        try {
            Map<String, Object> examStats = dashboardService.getExamStatistics();
            return ApiResponse.success("获取考试统计数据成功", examStats);
        } catch (Exception e) {
            return ApiResponse.error("获取考试统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取题目统计数据
     */
    @GetMapping("/question-stats")
    public ApiResponse<Map<String, Object>> getQuestionStats() {
        try {
            Map<String, Object> questionStats = dashboardService.getQuestionStatistics();
            return ApiResponse.success("获取题目统计数据成功", questionStats);
        } catch (Exception e) {
            return ApiResponse.error("获取题目统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近活动数据
     */
    @GetMapping("/recent-activities")
    public ApiResponse<Map<String, Object>> getRecentActivities() {
        try {
            Map<String, Object> activities = dashboardService.getRecentActivities();
            return ApiResponse.success("获取最近活动数据成功", activities);
        } catch (Exception e) {
            return ApiResponse.error("获取最近活动数据失败: " + e.getMessage());
        }
    }
}