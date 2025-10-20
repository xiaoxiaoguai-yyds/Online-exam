package org.bishe.question.service;

import org.bishe.question.dto.StudentLoginRequest;
import org.bishe.question.dto.StudentLoginResponse;
import org.bishe.question.entity.Student;
import org.bishe.question.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 学生服务类
 */
@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 学生登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    public StudentLoginResponse login(StudentLoginRequest loginRequest) {
        System.out.println("=== 学生登录请求开始 ===");
        System.out.println("请求学号: " + loginRequest.getStudentNumber());
        System.out.println("请求密码: " + loginRequest.getPassword());
        
        // 参数验证
        if (!StringUtils.hasText(loginRequest.getStudentNumber()) || 
            !StringUtils.hasText(loginRequest.getPassword())) {
            System.out.println("参数验证失败: 学号或密码为空");
            throw new RuntimeException("学号和密码不能为空");
        }
        
        // 查找学生（支持学号或邮箱登录）
        System.out.println("正在查找学生: " + loginRequest.getStudentNumber());
        Optional<Student> studentOptional = studentRepository.findByStudentNumberOrEmail(
            loginRequest.getStudentNumber(), loginRequest.getStudentNumber());
        
        if (studentOptional.isEmpty()) {
            System.out.println("学生查找失败: 学生不存在");
            throw new RuntimeException("学生不存在");
        }
        
        Student student = studentOptional.get();
        System.out.println("找到学生: " + student.getStudentNumber());
        System.out.println("数据库中的密码: " + student.getPassword());
        System.out.println("学生状态: " + student.getStatus());
        
        // 检查学生状态
        if (student.getStatus() == null || student.getStatus() != 1) {
            System.out.println("学生状态检查失败: 学生已被禁用");
            throw new RuntimeException("学生账号已被禁用");
        }
        
        // 验证密码（使用BCrypt加密验证）
        System.out.println("密码验证: 输入[" + loginRequest.getPassword() + "] vs 数据库[" + student.getPassword() + "]");
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), student.getPassword());
        System.out.println("密码是否匹配: " + passwordMatches);
        
        if (!passwordMatches) {
            System.out.println("密码验证失败: 密码不匹配");
            throw new RuntimeException("密码错误");
        }
        
        System.out.println("密码验证成功");
        
        // 更新最后登录时间（这里我们更新updatedAt字段）
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);
        
        // 构建登录响应
        StudentLoginResponse response = new StudentLoginResponse(
            student.getId(),
            student.getStudentNumber(),
            student.getName(),
            generateToken(student.getId())
        );
        response.setEmail(student.getEmail());
        response.setPhone(student.getPhone());
        response.setClassName(student.getClassName());
        response.setMajor(student.getMajor());
        response.setGrade(student.getGrade());
        
        System.out.println("学生登录成功: " + response.getStudentNumber());
        return response;
    }
    
    /**
     * 根据学号查找学生
     * @param studentNumber 学号
     * @return 学生信息
     */
    public Optional<Student> findByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }
    
    /**
     * 根据ID查找学生
     * @param id 学生ID
     * @return 学生信息
     */
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
    
    /**
     * 检查学号是否存在
     * @param studentNumber 学号
     * @return 是否存在
     */
    public boolean existsByStudentNumber(String studentNumber) {
        return studentRepository.existsByStudentNumber(studentNumber);
    }
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }
    
    /**
     * 根据班级查找学生
     * @param className 班级名称
     * @return 学生列表
     */
    public List<Student> findByClassName(String className) {
        return studentRepository.findByClassName(className);
    }
    
    /**
     * 根据专业查找学生
     * @param major 专业
     * @return 学生列表
     */
    public List<Student> findByMajor(String major) {
        return studentRepository.findByMajor(major);
    }
    
    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 生成token（简单实现，实际项目中应使用JWT）
     * @param studentId 学生ID
     * @return token
     */
    private String generateToken(Long studentId) {
        return "student_token_" + studentId + "_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    // ==================== 管理员功能 ====================
    
    /**
     * 分页查询所有学生
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页学生列表
     */
    public Page<Student> findAllStudents(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findAll(pageable);
    }
    
    /**
     * 根据关键词搜索学生（支持姓名、学号、邮箱、班级、专业搜索）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 搜索结果
     */
    public Page<Student> searchStudents(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findByKeyword(keyword, pageable);
    }
    
    /**
     * 根据状态查询学生
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页学生列表
     */
    public Page<Student> findStudentsByStatus(Integer status, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findByStatus(status, pageable);
    }
    
    /**
     * 根据班级查询学生
     * @param className 班级名称
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页学生列表
     */
    public Page<Student> findStudentsByClassName(String className, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findByClassName(className, pageable);
    }
    
    /**
     * 根据专业查询学生
     * @param major 专业
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页学生列表
     */
    public Page<Student> findStudentsByMajor(String major, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findByMajor(major, pageable);
    }
    
    /**
     * 获取所有学生（不分页）
     * @return 所有学生列表
     */
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }
    
    /**
     * 更新学生状态
     * @param studentId 学生ID
     * @param status 新状态
     * @return 更新后的学生信息
     */
    public Student updateStudentStatus(Long studentId, Integer status) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("学生不存在");
        }
        
        Student student = studentOptional.get();
        student.setStatus(status);
        student.setUpdatedAt(LocalDateTime.now());
        return studentRepository.save(student);
    }
    
    /**
     * 删除学生
     * @param studentId 学生ID
     */
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("学生不存在");
        }
        studentRepository.deleteById(studentId);
    }
    
    /**
     * 批量删除学生
     * @param studentIds 学生ID列表
     */
    public void deleteStudents(List<Long> studentIds) {
        studentRepository.deleteAllById(studentIds);
    }
    
    /**
     * 获取学生统计信息
     * @return 统计信息
     */
    public StudentStatistics getStudentStatistics() {
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(1);
        long inactiveStudents = studentRepository.countByStatus(0);
        
        return new StudentStatistics(totalStudents, activeStudents, inactiveStudents);
    }
    
    /**
     * 获取所有不同的班级列表
     * @return 班级列表
     */
    public List<String> findDistinctClassNames() {
        return studentRepository.findDistinctClassNames();
    }
    
    /**
     * 获取所有不同的专业列表
     * @return 专业列表
     */
    public List<String> findDistinctMajors() {
        return studentRepository.findDistinctMajors();
    }
    
    /**
     * 获取所有不同的年级列表
     * @return 年级列表
     */
    public List<String> findDistinctGrades() {
        return studentRepository.findDistinctGrades();
    }
    
    /**
     * 学生统计信息内部类
     */
    public static class StudentStatistics {
        private long totalStudents;
        private long activeStudents;
        private long inactiveStudents;
        
        public StudentStatistics(long totalStudents, long activeStudents, long inactiveStudents) {
            this.totalStudents = totalStudents;
            this.activeStudents = activeStudents;
            this.inactiveStudents = inactiveStudents;
        }
        
        // Getters
        public long getTotalStudents() { return totalStudents; }
        public long getActiveStudents() { return activeStudents; }
        public long getInactiveStudents() { return inactiveStudents; }
    }
}