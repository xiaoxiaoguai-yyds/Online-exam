package org.bishe.question.repository;

import org.bishe.question.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生数据访问层
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * 根据学号查找学生
     * @param studentNumber 学号
     * @return 学生信息
     */
    Optional<Student> findByStudentNumber(String studentNumber);
    
    /**
     * 根据邮箱查找学生
     * @param email 邮箱
     * @return 学生信息
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * 根据学号或邮箱查找学生
     * @param studentNumber 学号
     * @param email 邮箱
     * @return 学生信息
     */
    @Query("SELECT s FROM Student s WHERE s.studentNumber = :studentNumber OR s.email = :email")
    Optional<Student> findByStudentNumberOrEmail(@Param("studentNumber") String studentNumber, @Param("email") String email);
    
    /**
     * 检查学号是否存在
     * @param studentNumber 学号
     * @return 是否存在
     */
    boolean existsByStudentNumber(String studentNumber);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查找学生
     * @param status 状态
     * @return 学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.status = :status")
    List<Student> findByStatus(@Param("status") Integer status);
    
    /**
     * 根据班级查找学生
     * @param className 班级名称
     * @return 学生列表
     */
    List<Student> findByClassName(String className);
    
    /**
     * 根据专业查找学生
     * @param major 专业
     * @return 学生列表
     */
    List<Student> findByMajor(String major);
    
    /**
     * 根据年级查找学生
     * @param grade 年级
     * @return 学生列表
     */
    List<Student> findByGrade(String grade);
    
    /**
     * 根据姓名模糊查找学生
     * @param name 姓名关键字
     * @return 学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.name LIKE %:name%")
    List<Student> findByNameContaining(@Param("name") String name);
    
    // ==================== 分页查询方法 ====================
    
    /**
     * 根据状态分页查询学生
     * @param status 状态
     * @param pageable 分页参数
     * @return 分页学生列表
     */
    Page<Student> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据班级分页查询学生
     * @param className 班级名称
     * @param pageable 分页参数
     * @return 分页学生列表
     */
    Page<Student> findByClassName(String className, Pageable pageable);
    
    /**
     * 根据专业分页查询学生
     * @param major 专业
     * @param pageable 分页参数
     * @return 分页学生列表
     */
    Page<Student> findByMajor(String major, Pageable pageable);
    
    /**
     * 根据年级分页查询学生
     * @param grade 年级
     * @param pageable 分页参数
     * @return 分页学生列表
     */
    Page<Student> findByGrade(String grade, Pageable pageable);
    
    /**
     * 根据关键词搜索学生（支持姓名、学号、邮箱、班级、专业搜索）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    @Query("SELECT s FROM Student s WHERE " +
           "s.name LIKE %:keyword% OR " +
           "s.studentNumber LIKE %:keyword% OR " +
           "s.email LIKE %:keyword% OR " +
           "s.className LIKE %:keyword% OR " +
           "s.major LIKE %:keyword%")
    Page<Student> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据状态统计学生数量
     * @param status 状态
     * @return 学生数量
     */
    long countByStatus(Integer status);
    
    /**
     * 根据班级统计学生数量
     * @param className 班级名称
     * @return 学生数量
     */
    long countByClassName(String className);
    
    /**
     * 根据专业统计学生数量
     * @param major 专业
     * @return 学生数量
     */
    long countByMajor(String major);
    
    /**
     * 获取所有不同的班级列表
     * @return 班级列表
     */
    @Query("SELECT DISTINCT s.className FROM Student s WHERE s.className IS NOT NULL ORDER BY s.className")
    List<String> findDistinctClassNames();
    
    /**
     * 获取所有不同的专业列表
     * @return 专业列表
     */
    @Query("SELECT DISTINCT s.major FROM Student s WHERE s.major IS NOT NULL ORDER BY s.major")
    List<String> findDistinctMajors();
    
    /**
     * 获取所有不同的年级列表
     * @return 年级列表
     */
    @Query("SELECT DISTINCT s.grade FROM Student s WHERE s.grade IS NOT NULL ORDER BY s.grade")
    List<String> findDistinctGrades();
}