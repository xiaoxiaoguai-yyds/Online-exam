package org.bishe.question.repository;

import org.bishe.question.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生答题记录Repository接口
 */
@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    
    /**
     * 根据考试记录ID查找所有答题记录
     */
    List<StudentAnswer> findByExamRecordId(Long examRecordId);
    
    /**
     * 根据考试记录ID按题目ID排序查找答题记录
     */
    List<StudentAnswer> findByExamRecordIdOrderByQuestionId(Long examRecordId);
    
    /**
     * 根据题目ID查找所有答题记录
     */
    List<StudentAnswer> findByQuestionId(Long questionId);
    
    /**
     * 根据考试记录ID和题目ID查找答题记录
     */
    Optional<StudentAnswer> findByExamRecordIdAndQuestionId(Long examRecordId, Long questionId);
    
    /**
     * 统计考试记录的答题数量
     */
    Long countByExamRecordId(Long examRecordId);
    
    /**
     * 统计题目的答题次数
     */
    Long countByQuestionId(Long questionId);
    
    /**
     * 统计考试记录的正确答题数量
     */
    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId AND sa.isCorrect = true")
    Long countCorrectByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 统计考试记录的错误答题数量
     */
    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId AND sa.isCorrect = false")
    Long countWrongByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 统计考试记录的未答题数量
     */
    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId AND (sa.studentAnswer IS NULL OR sa.studentAnswer = '')")
    Long countUnansweredByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 统计题目的正确率
     */
    @Query("SELECT (COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) * 100.0 / COUNT(sa)) " +
           "FROM StudentAnswer sa WHERE sa.questionId = :questionId AND sa.isCorrect IS NOT NULL")
    Double getCorrectRateByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 查找考试记录中已回答的题目
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId " +
           "AND sa.studentAnswer IS NOT NULL AND sa.studentAnswer != ''")
    List<StudentAnswer> findAnsweredByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 查找考试记录中未回答的题目
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId " +
           "AND (sa.studentAnswer IS NULL OR sa.studentAnswer = '')")
    List<StudentAnswer> findUnansweredByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 查找考试记录中正确的答题
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId AND sa.isCorrect = true")
    List<StudentAnswer> findCorrectByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 查找考试记录中错误的答题
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId AND sa.isCorrect = false")
    List<StudentAnswer> findWrongByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 检查考试记录是否存在指定题目的答案
     */
    boolean existsByExamRecordIdAndQuestionId(Long examRecordId, Long questionId);
    
    /**
     * 查找指定时间范围内的答题记录
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.answerTime >= :startTime AND sa.answerTime <= :endTime")
    List<StudentAnswer> findByAnswerTimeRange(@Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找最近的答题记录
     */
    @Query("SELECT sa FROM StudentAnswer sa ORDER BY sa.answerTime DESC")
    List<StudentAnswer> findRecentAnswers(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查找考试记录的最近答题
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId ORDER BY sa.answerTime DESC")
    List<StudentAnswer> findRecentAnswersByExamRecord(@Param("examRecordId") Long examRecordId, 
                                                     org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查找题目的最近答题记录
     */
    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.questionId = :questionId ORDER BY sa.answerTime DESC")
    List<StudentAnswer> findRecentAnswersByQuestion(@Param("questionId") Long questionId, 
                                                   org.springframework.data.domain.Pageable pageable);
    
    /**
     * 更新学生答案
     */
    @Modifying
    @Query("UPDATE StudentAnswer sa SET sa.studentAnswer = :studentAnswer, sa.answerTime = :answerTime, " +
           "sa.updatedAt = :updateTime WHERE sa.examRecordId = :examRecordId AND sa.questionId = :questionId")
    int updateStudentAnswer(@Param("examRecordId") Long examRecordId,
                           @Param("questionId") Long questionId,
                           @Param("studentAnswer") String studentAnswer,
                           @Param("answerTime") LocalDateTime answerTime,
                           @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 更新答案正确性
     */
    @Modifying
    @Query("UPDATE StudentAnswer sa SET sa.isCorrect = :isCorrect, sa.updatedAt = :updateTime " +
           "WHERE sa.examRecordId = :examRecordId AND sa.questionId = :questionId")
    int updateCorrectness(@Param("examRecordId") Long examRecordId,
                         @Param("questionId") Long questionId,
                         @Param("isCorrect") Boolean isCorrect,
                         @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量更新考试记录的正确答案
     */
    @Modifying
    @Query("UPDATE StudentAnswer sa SET sa.correctAnswer = :correctAnswer, sa.updatedAt = :updateTime " +
           "WHERE sa.examRecordId = :examRecordId AND sa.questionId = :questionId")
    int updateCorrectAnswer(@Param("examRecordId") Long examRecordId,
                           @Param("questionId") Long questionId,
                           @Param("correctAnswer") String correctAnswer,
                           @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 清空学生答案
     */
    @Modifying
    @Query("UPDATE StudentAnswer sa SET sa.studentAnswer = NULL, sa.answerTime = NULL, " +
           "sa.isCorrect = NULL, sa.updatedAt = :updateTime " +
           "WHERE sa.examRecordId = :examRecordId AND sa.questionId = :questionId")
    int clearAnswer(@Param("examRecordId") Long examRecordId,
                   @Param("questionId") Long questionId,
                   @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 根据考试记录ID删除所有答题记录
     */
    @Modifying
    @Query("DELETE FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId")
    int deleteByExamRecordId(@Param("examRecordId") Long examRecordId);
    
    /**
     * 根据题目ID删除所有答题记录
     */
    @Modifying
    @Query("DELETE FROM StudentAnswer sa WHERE sa.questionId = :questionId")
    int deleteByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 删除指定考试记录的指定题目答案
     */
    @Modifying
    @Query("DELETE FROM StudentAnswer sa WHERE sa.examRecordId = :examRecordId AND sa.questionId = :questionId")
    int deleteByExamRecordIdAndQuestionId(@Param("examRecordId") Long examRecordId, 
                                         @Param("questionId") Long questionId);
    
    /**
     * 查找题目答案分布统计
     */
    @Query("SELECT sa.studentAnswer, COUNT(sa) FROM StudentAnswer sa " +
           "WHERE sa.questionId = :questionId AND sa.studentAnswer IS NOT NULL " +
           "GROUP BY sa.studentAnswer ORDER BY COUNT(sa) DESC")
    List<Object[]> getAnswerDistributionByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 查找考试中每道题的答题情况统计
     */
    @Query("SELECT sa.questionId, " +
           "COUNT(sa) as totalAnswers, " +
           "COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) as correctAnswers, " +
           "COUNT(CASE WHEN sa.isCorrect = false THEN 1 END) as wrongAnswers, " +
           "COUNT(CASE WHEN sa.studentAnswer IS NULL OR sa.studentAnswer = '' THEN 1 END) as unansweredCount " +
           "FROM StudentAnswer sa " +
           "WHERE sa.examRecordId IN (SELECT er.id FROM ExamRecord er WHERE er.examId = :examId) " +
           "GROUP BY sa.questionId")
    List<Object[]> getQuestionStatisticsByExamId(@Param("examId") Long examId);
}