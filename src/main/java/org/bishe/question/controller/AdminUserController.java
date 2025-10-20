package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.entity.User;
import org.bishe.question.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询所有用户
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页用户列表
     */
    @GetMapping
    public ApiResponse<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<User> users = userService.findAllUsers(page, size, sortBy, sortDir);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ApiResponse<Page<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<User> users = userService.searchUsers(keyword, page, size, sortBy, sortDir);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("搜索用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据状态查询用户
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页用户列表
     */
    @GetMapping("/status/{status}")
    public ApiResponse<Page<User>> getUsersByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<User> users = userService.findUsersByStatus(status, page, size, sortBy, sortDir);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户统计信息
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> statistics = userService.getUserStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        try {
            return userService.findById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("用户不存在"));
        } catch (Exception e) {
            return ApiResponse.error("获取用户详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<User> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            User updatedUser = userService.updateUserStatus(id, status);
            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            return ApiResponse.error("更新用户状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近活跃用户
     * @param days 天数
     * @param page 页码
     * @param size 每页大小
     * @return 最近活跃用户列表
     */
    @GetMapping("/recent-active")
    public ApiResponse<Page<User>> getRecentlyActiveUsers(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<User> users = userService.getRecentlyActiveUsers(days, page, size);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("获取最近活跃用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取从未登录的用户
     * @param page 页码
     * @param size 每页大小
     * @return 从未登录的用户列表
     */
    @GetMapping("/never-logged-in")
    public ApiResponse<Page<User>> getNeverLoggedInUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<User> users = userService.getNeverLoggedInUsers(page, size);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("获取从未登录用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据创建时间范围查询用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    @GetMapping("/created-between")
    public ApiResponse<Page<User>> getUsersByCreatedTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<User> users = userService.findUsersByCreatedTime(startTime, endTime, page, size);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }
}