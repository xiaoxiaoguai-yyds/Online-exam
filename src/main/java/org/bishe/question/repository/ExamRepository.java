package org.bishe.question.repository;

import org.bishe.question.entity.Exam;
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
 * 考试Repository接口
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    /**
     * 根据状态查找考试
     */
    List<Exam> findByStatus(Integer status);
    
    /**
     * 根据状态分页查找考试
     */
    Page<Exam> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据创建者查找考试
     */
    List<Exam> findByCreatedBy(Long createdBy);
    
    /**
     * 根据创建者分页查找考试
     */
    Page<Exam> findByCreatedBy(Long createdBy, Pageable pageable);
    
    /**
     * 根据标题模糊查询
     */
    @Query("SELECT e FROM Exam e WHERE e.title LIKE %:title%")
    List<Exam> findByTitleContaining(@Param("title") String title);
    
    /**
     * 根据标题模糊查询（分页）
     */
    @Query("SELECT e FROM Exam e WHERE e.title LIKE %:title%")
    Page<Exam> findByTitleContaining(@Param("title") String title, Pageable pageable);
    
    /**
     * 查找正在进行的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.status = 1 AND e.startTime <= :now AND e.endTime > :now")
    List<Exam> findActiveExams(@Param("now") LocalDateTime now);
    
    /**
     * 查找即将开始的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.status = 1 AND e.startTime > :now")
    List<Exam> findUpcomingExams(@Param("now") LocalDateTime now);
    
    /**
     * 查找已结束的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.status = 2 OR e.endTime <= :now")
    List<Exam> findFinishedExams(@Param("now") LocalDateTime now);
    
    /**
     * 根据时间范围查找考试
     */
    @Query("SELECT e FROM Exam e WHERE e.startTime >= :startTime AND e.endTime <= :endTime")
    List<Exam> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找学生可参加的考试（未参加过的正在进行或即将开始的考试）
     * 修改：只排除已完成的考试记录（状态为2或3），允许重置后的考试记录（状态为0）重新参加
     */
    @Query("SELECT e FROM Exam e WHERE e.status = 1 AND e.endTime > :now " +
           "AND NOT EXISTS (SELECT er FROM ExamRecord er WHERE er.examId = e.id AND er.studentId = :studentId AND er.status IN (2, 3))")
    List<Exam> findAvailableExamsForStudent(@Param("studentId") Long studentId, 
                                           @Param("now") LocalDateTime now);
    
    /**
     * 查找学生已参加的考试
     */
    @Query("SELECT e FROM Exam e WHERE EXISTS (SELECT er FROM ExamRecord er WHERE er.examId = e.id AND er.studentId = :studentId)")
    List<Exam> findExamsByStudent(@Param("studentId") Long studentId);
    
    /**
     * 统计考试总数
     */
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.status = :status")
    Long countByStatus(@Param("status") Integer status);
    
    /**
     * 根据创建者查找考试数量
     */
    Long countByCreatedBy(Long createdBy);
    
    /**
     * 获取最近创建的考试（前5个）
     */
    List<Exam> findTop5ByOrderByCreatedAtDesc();
    
    /**
     * 查找最近创建的考试
     */
    @Query("SELECT e FROM Exam e ORDER BY e.createdAt DESC")
    List<Exam> findRecentExams(Pageable pageable);
    
    /**
     * 根据ID和创建者查找考试（用于权限验证）
     */
    Optional<Exam> findByIdAndCreatedBy(Long id, Long createdBy);
    
    /**
     * 检查考试标题是否存在
     */
    boolean existsByTitle(String title);
    
    /**
     * 检查考试标题是否存在（排除指定ID）
     */
    @Query("SELECT COUNT(e) > 0 FROM Exam e WHERE e.title = :title AND e.id != :excludeId")
    boolean existsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Long excludeId);
    
    /**
     * 更新考试状态
     */
    @Query("UPDATE Exam e SET e.status = :status, e.updatedAt = :updateTime WHERE e.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量更新过期考试状态
     */
    @Query("UPDATE Exam e SET e.status = 2, e.updatedAt = :updateTime WHERE e.status = 1 AND e.endTime <= :now")
    int updateExpiredExamsStatus(@Param("now") LocalDateTime now, @Param("updateTime") LocalDateTime updateTime);
}