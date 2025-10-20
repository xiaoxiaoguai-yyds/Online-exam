package org.bishe.question.service;

import org.bishe.question.dto.LoginRequest;
import org.bishe.question.dto.LoginResponse;
import org.bishe.question.entity.User;
import org.bishe.question.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户服务类
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("=== 登录请求开始 ===");
        System.out.println("请求用户名: " + loginRequest.getUsername());
        System.out.println("请求密码: " + loginRequest.getPassword());
        
        // 参数验证
        if (!StringUtils.hasText(loginRequest.getUsername()) || 
            !StringUtils.hasText(loginRequest.getPassword())) {
            System.out.println("参数验证失败: 用户名或密码为空");
            throw new RuntimeException("用户名和密码不能为空");
        }
        
        // 查找用户（支持用户名或邮箱登录）
        System.out.println("正在查找用户: " + loginRequest.getUsername());
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(
            loginRequest.getUsername(), loginRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            System.out.println("用户查找失败: 用户不存在");
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOptional.get();
        System.out.println("找到用户: " + user.getUsername());
        System.out.println("数据库中的密码: " + user.getPassword());
        System.out.println("用户状态: " + user.getStatus());
        
        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            System.out.println("用户状态检查失败: 用户已被禁用");
            throw new RuntimeException("用户已被禁用");
        }
        
        // 验证密码（直接比较明文密码）
        System.out.println("密码比较: 输入[" + loginRequest.getPassword() + "] vs 数据库[" + user.getPassword() + "]");
        System.out.println("密码是否相等: " + loginRequest.getPassword().equals(user.getPassword()));
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            System.out.println("密码验证失败: 密码不匹配");
            throw new RuntimeException("密码错误");
        }
        
        System.out.println("密码验证成功");
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 构建登录响应
        LoginResponse response = new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail()
        );
        response.setAvatar(user.getAvatar());
        
        // 生成简单的token（实际项目中应使用JWT）
        String token = generateToken(user.getId());
        response.setToken(token);
        
        return response;
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * 密码加密
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 生成token（简单实现，实际项目中应使用JWT）
     * @param userId 用户ID
     * @return token
     */
    private String generateToken(Long userId) {
        return "token_" + userId + "_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    // ==================== 管理员功能 ====================
    
    /**
     * 分页查询所有用户
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页用户列表
     */
    public Page<User> findAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }
    
    /**
     * 根据关键词搜索用户（支持用户名、昵称、邮箱搜索）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 搜索结果
     */
    public Page<User> searchUsers(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByKeyword(keyword, pageable);
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
    public Page<User> findUsersByStatus(Integer status, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByStatus(status, pageable);
    }
    
    /**
     * 获取用户统计信息
     * @return 统计信息
     */
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总用户数
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // 按状态统计
        long activeUsers = userRepository.countByStatus(1);
        long inactiveUsers = userRepository.countByStatus(0);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        
        // 最近活跃用户（最近7天登录）
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<User> recentActiveUsers = userRepository.findRecentlyActiveUsers(lastWeek, pageable);
        stats.put("recentActiveUsers", recentActiveUsers.getTotalElements());
        
        // 从未登录的用户
        Page<User> neverLoggedInUsers = userRepository.findNeverLoggedInUsers(pageable);
        stats.put("neverLoggedInUsers", neverLoggedInUsers.getTotalElements());
        
        return stats;
    }
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 新状态
     * @return 更新后的用户
     */
    public User updateUserStatus(Long userId, Integer status) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOptional.get();
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    /**
     * 获取最近登录的用户
     * @param days 天数
     * @param page 页码
     * @param size 每页大小
     * @return 最近登录的用户列表
     */
    public Page<User> getRecentlyActiveUsers(int days, int page, int size) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastLoginTime"));
        return userRepository.findRecentlyActiveUsers(since, pageable);
    }
    
    /**
     * 获取从未登录的用户
     * @param page 页码
     * @param size 每页大小
     * @return 从未登录的用户列表
     */
    public Page<User> getNeverLoggedInUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        return userRepository.findNeverLoggedInUsers(pageable);
    }
    
    /**
     * 根据创建时间范围查询用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    public Page<User> findUsersByCreatedTime(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        return userRepository.findByCreatedTimeBetween(startTime, endTime, pageable);
    }
}