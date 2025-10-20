package org.bishe.question.repository;

import org.bishe.question.entity.ExamRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考试记录Repository接口
 */
@Repository
public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {
    
    /**
     * 根据考试ID查找所有记录
     */
    List<ExamRecord> findByExamId(Long examId);
    
    /**
     * 根据考试ID分页查找记录
     */
    Page<ExamRecord> findByExamId(Long examId, Pageable pageable);
    
    /**
     * 根据学生ID查找所有记录
     */
    List<ExamRecord> findByStudentId(Long studentId);
    
    /**
     * 根据学生ID分页查找记录
     */
    Page<ExamRecord> findByStudentId(Long studentId, Pageable pageable);
    
    /**
     * 根据考试ID和学生ID查找记录
     */
    Optional<ExamRecord> findByExamIdAndStudentId(Long examId, Long studentId);
    
    /**
     * 根据状态查找记录
     */
    List<ExamRecord> findByStatus(Integer status);
    
    /**
     * 根据状态分页查找记录
     */
    Page<ExamRecord> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据考试ID和状态查找记录
     */
    List<ExamRecord> findByExamIdAndStatus(Long examId, Integer status);
    
    /**
     * 根据考试ID和状态分页查找记录
     */
    Page<ExamRecord> findByExamIdAndStatus(Long examId, Integer status, Pageable pageable);
    
    /**
     * 根据学生姓名模糊查找记录
     */
    List<ExamRecord> findByStudentNameContaining(String studentName);
    
    /**
     * 根据学生姓名模糊分页查找记录
     */
    Page<ExamRecord> findByStudentNameContaining(String studentName, Pageable pageable);
    
    /**
     * 根据学生姓名和状态查找记录
     */
    List<ExamRecord> findByStudentNameContainingAndStatus(String studentName, Integer status);
    
    /**
     * 根据学生姓名和状态分页查找记录
     */
    Page<ExamRecord> findByStudentNameContainingAndStatus(String studentName, Integer status, Pageable pageable);
    
    /**
     * 根据考试ID和学生姓名查找记录
     */
    List<ExamRecord> findByExamIdAndStudentNameContaining(Long examId, String studentName);
    
    /**
     * 根据考试ID和学生姓名分页查找记录
     */
    Page<ExamRecord> findByExamIdAndStudentNameContaining(Long examId, String studentName, Pageable pageable);
    
    /**
     * 根据考试ID、学生姓名和状态查找记录
     */
    List<ExamRecord> findByExamIdAndStudentNameContainingAndStatus(Long examId, String studentName, Integer status);
    
    /**
     * 根据考试ID、学生姓名和状态分页查找记录
     */
    Page<ExamRecord> findByExamIdAndStudentNameContainingAndStatus(Long examId, String studentName, Integer status, Pageable pageable);
    
    /**
     * 统计指定考试和状态的记录数
     */
    Long countByExamIdAndStatus(Long examId, Integer status);
    
    /**
     * 统计指定考试、状态且分数大于等于指定值的记录数
     */
    Long countByExamIdAndStatusAndTotalScoreGreaterThanEqual(Long examId, Integer status, BigDecimal minScore);
    
    /**
     * 查找正在进行中的考试记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.status = 1")
    List<ExamRecord> findInProgressRecords();
    
    /**
     * 查找已完成的考试记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.status IN (2, 3)")
    List<ExamRecord> findCompletedRecords();
    
    /**
     * 查找学生的已完成考试记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.studentId = :studentId AND er.status IN (2, 3)")
    List<ExamRecord> findCompletedRecordsByStudent(@Param("studentId") Long studentId);
    
    /**
     * 查找考试的已完成记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.examId = :examId AND er.status IN (2, 3)")
    List<ExamRecord> findCompletedRecordsByExam(@Param("examId") Long examId);
    
    /**
     * 统计考试参与人数
     */
    Long countByExamId(Long examId);
    
    /**
     * 统计考试完成人数
     */
    @Query("SELECT COUNT(er) FROM ExamRecord er WHERE er.examId = :examId AND er.status IN (2, 3)")
    Long countCompletedByExamId(@Param("examId") Long examId);
    
    /**
     * 统计学生参与考试次数
     */
    Long countByStudentId(Long studentId);
    
    /**
     * 统计学生完成考试次数
     */
    @Query("SELECT COUNT(er) FROM ExamRecord er WHERE er.studentId = :studentId AND er.status IN (2, 3)")
    Long countCompletedByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 检查学生是否已参加过考试
     */
    boolean existsByExamIdAndStudentId(Long examId, Long studentId);
    
    /**
     * 获取考试平均分
     */
    @Query("SELECT AVG(er.score) FROM ExamRecord er WHERE er.examId = :examId AND er.status IN (2, 3) AND er.score IS NOT NULL")
    BigDecimal getAverageScoreByExamId(@Param("examId") Long examId);
    
    /**
     * 根据考试ID和状态获取平均分
     */
    @Query("SELECT AVG(er.score) FROM ExamRecord er WHERE er.examId = :examId AND er.status = :status AND er.score IS NOT NULL")
    BigDecimal getAverageScoreByExamIdAndStatus(@Param("examId") Long examId, @Param("status") Integer status);
    
    /**
     * 获取考试最高分
     */
    @Query("SELECT MAX(er.score) FROM ExamRecord er WHERE er.examId = :examId AND er.status IN (2, 3)")
    BigDecimal getMaxScoreByExamId(@Param("examId") Long examId);
    
    /**
     * 根据考试ID和状态获取最高分
     */
    @Query("SELECT MAX(er.score) FROM ExamRecord er WHERE er.examId = :examId AND er.status = :status")
    BigDecimal getMaxScoreByExamIdAndStatus(@Param("examId") Long examId, @Param("status") Integer status);
    
    /**
     * 获取考试最低分
     */
    @Query("SELECT MIN(er.score) FROM ExamRecord er WHERE er.examId = :examId AND er.status IN (2, 3)")
    BigDecimal getMinScoreByExamId(@Param("examId") Long examId);
    
    /**
     * 根据考试ID和状态获取最低分
     */
    @Query("SELECT MIN(er.score) FROM ExamRecord er WHERE er.examId = :examId AND er.status = :status")
    BigDecimal getMinScoreByExamIdAndStatus(@Param("examId") Long examId, @Param("status") Integer status);
    
    /**
     * 查找学生的平均分
     */
    @Query("SELECT AVG(er.score) FROM ExamRecord er WHERE er.studentId = :studentId AND er.status IN (2, 3) AND er.score IS NOT NULL")
    BigDecimal getAverageScoreByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 查找考试及格人数
     */
    @Query("SELECT COUNT(er) FROM ExamRecord er JOIN Exam e ON er.examId = e.id " +
           "WHERE er.examId = :examId AND er.status IN (2, 3) AND er.score >= e.passScore")
    Long countPassedByExamId(@Param("examId") Long examId);
    
    /**
     * 查找考试不及格人数
     */
    @Query("SELECT COUNT(er) FROM ExamRecord er JOIN Exam e ON er.examId = e.id " +
           "WHERE er.examId = :examId AND er.status IN (2, 3) AND er.score < e.passScore")
    Long countFailedByExamId(@Param("examId") Long examId);
    
    /**
     * 查找超时提交的记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.status = 3")
    List<ExamRecord> findTimeoutRecords();
    
    /**
     * 查找指定时间范围内的考试记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.startTime >= :startTime AND er.startTime <= :endTime")
    List<ExamRecord> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找最近的考试记录
     */
    @Query("SELECT er FROM ExamRecord er ORDER BY er.createdAt DESC")
    List<ExamRecord> findRecentRecords(Pageable pageable);
    
    /**
     * 根据学生ID查找最近的考试记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.studentId = :studentId ORDER BY er.createdAt DESC")
    List<ExamRecord> findRecentRecordsByStudent(@Param("studentId") Long studentId, Pageable pageable);
    
    /**
     * 根据学生ID查找最近的考试记录（限制数量）
     */
    @Query("SELECT er FROM ExamRecord er JOIN FETCH er.exam WHERE er.studentId = :studentId ORDER BY er.createdAt DESC")
    List<ExamRecord> findRecentExamRecordsByStudentId(@Param("studentId") Long studentId, Pageable pageable);
    
    /**
     * 查找考试最近的记录
     */
    @Query("SELECT er FROM ExamRecord er WHERE er.examId = :examId ORDER BY er.createdAt DESC")
    List<ExamRecord> findRecentRecordsByExam(@Param("examId") Long examId, Pageable pageable);
    
    /**
     * 更新考试记录状态
     */
    @Modifying
    @Query("UPDATE ExamRecord er SET er.status = :status, er.updatedAt = :updateTime WHERE er.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 更新考试记录分数
     */
    @Modifying
    @Query("UPDATE ExamRecord er SET er.score = :score, er.correctCount = :correctCount, " +
           "er.wrongCount = :wrongCount, er.unansweredCount = :unansweredCount, er.updatedAt = :updateTime " +
           "WHERE er.id = :id")
    int updateScore(@Param("id") Long id, 
                   @Param("score") BigDecimal score,
                   @Param("correctCount") Integer correctCount,
                   @Param("wrongCount") Integer wrongCount,
                   @Param("unansweredCount") Integer unansweredCount,
                   @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量超时提交正在进行的考试
     */
    @Modifying
    @Query("UPDATE ExamRecord er SET er.status = 3, er.submitTime = :submitTime, " +
           "er.endTime = :submitTime, er.updatedAt = :updateTime " +
           "WHERE er.status = 1 AND er.startTime IS NOT NULL " +
           "AND TIMESTAMPDIFF(MINUTE, er.startTime, :submitTime) >= " +
           "(SELECT e.duration FROM Exam e WHERE e.id = er.examId)")
    int timeoutInProgressRecords(@Param("submitTime") LocalDateTime submitTime, 
                                @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 根据考试ID删除所有记录
     */
    @Modifying
    @Query("DELETE FROM ExamRecord er WHERE er.examId = :examId")
    int deleteByExamId(@Param("examId") Long examId);
    
    /**
     * 根据学生ID删除所有记录
     */
    @Modifying
    @Query("DELETE FROM ExamRecord er WHERE er.studentId = :studentId")
    int deleteByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据状态统计记录数量
     */
    Long countByStatus(Integer status);
    
    /**
     * 获取最近的考试记录（前10个）
     */
    List<ExamRecord> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根据创建时间范围查找记录
     */
    List<ExamRecord> findByCreatedAtAfter(LocalDateTime createdAt);
    
    /**
     * 根据创建时间范围统计记录数量
     */
    Long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据状态和提交时间范围统计记录数量
     */
    Long countByStatusAndSubmitTimeBetween(Integer status, LocalDateTime startTime, LocalDateTime endTime);
}