package org.bishe.question.repository;

import org.bishe.question.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     * @param username 用户名
     * @param email 邮箱
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查找用户
     * @param status 状态
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") Integer status);
    
    // ==================== 分页查询方法 ====================
    
    /**
     * 根据状态分页查询用户
     * @param status 状态
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    Page<User> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据关键词搜索用户（支持用户名、昵称、邮箱搜索）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.username LIKE %:keyword% OR " +
           "u.nickname LIKE %:keyword% OR " +
           "u.email LIKE %:keyword%")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据状态统计用户数量
     * @param status 状态
     * @return 用户数量
     */
    long countByStatus(Integer status);
    
    /**
     * 查找最近登录的用户
     * @param since 时间起点
     * @param pageable 分页参数
     * @return 最近登录的用户列表
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginTime >= :since ORDER BY u.lastLoginTime DESC")
    Page<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since, Pageable pageable);
    
    /**
     * 查找从未登录的用户
     * @param pageable 分页参数
     * @return 从未登录的用户列表
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginTime IS NULL")
    Page<User> findNeverLoggedInUsers(Pageable pageable);
    
    /**
     * 根据创建时间范围查询用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.createdTime BETWEEN :startTime AND :endTime")
    Page<User> findByCreatedTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime, 
                                       Pageable pageable);
}