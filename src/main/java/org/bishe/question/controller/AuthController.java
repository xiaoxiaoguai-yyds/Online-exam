package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.dto.LoginRequest;
import org.bishe.question.dto.LoginResponse;
import org.bishe.question.dto.StudentLoginRequest;
import org.bishe.question.dto.StudentLoginResponse;
import org.bishe.question.service.UserService;
import org.bishe.question.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentService studentService;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest);
            return ApiResponse.success("登录成功", loginResponse);
        } catch (RuntimeException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("登录失败，请稍后重试");
        }
    }
    
    /**
     * 学生登录
     * @param loginRequest 学生登录请求
     * @return 学生登录响应
     */
    @PostMapping("/student/login")
    public ApiResponse<StudentLoginResponse> studentLogin(@Valid @RequestBody StudentLoginRequest loginRequest) {
        try {
            System.out.println("=== 收到学生登录请求 ===");
            System.out.println("学号: " + loginRequest.getStudentNumber());
            StudentLoginResponse loginResponse = studentService.login(loginRequest);
            return ApiResponse.success("学生登录成功", loginResponse);
        } catch (RuntimeException e) {
            System.out.println("学生登录失败: " + e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            System.out.println("学生登录异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("登录失败，请稍后重试");
        }
    }
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            return ApiResponse.error("检查失败");
        }
    }
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            return ApiResponse.error("检查失败");
        }
    }
    
    /**
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("服务正常运行");
    }
    
    /**
     * 用户登出
     * @return 登出响应
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        // 实际项目中应该清除token或session
        return ApiResponse.success("登出成功");
    }
}