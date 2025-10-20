package org.bishe.question.repository;

import org.bishe.question.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 考试题目关联Repository接口
 */
@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    
    /**
     * 根据考试ID查找所有题目
     */
    List<ExamQuestion> findByExamId(Long examId);
    
    /**
     * 根据考试ID按顺序查找所有题目
     */
    List<ExamQuestion> findByExamIdOrderByQuestionOrder(Long examId);
    
    /**
     * 根据题目ID查找所有关联的考试
     */
    List<ExamQuestion> findByQuestionId(Long questionId);
    
    /**
     * 根据考试ID和题目ID查找关联记录
     */
    Optional<ExamQuestion> findByExamIdAndQuestionId(Long examId, Long questionId);
    
    /**
     * 根据考试ID和题目顺序查找题目
     */
    Optional<ExamQuestion> findByExamIdAndQuestionOrder(Long examId, Integer questionOrder);
    
    /**
     * 统计考试的题目数量
     */
    Long countByExamId(Long examId);
    
    /**
     * 统计题目被使用的次数
     */
    Long countByQuestionId(Long questionId);
    
    /**
     * 计算考试总分
     */
    @Query("SELECT SUM(eq.score) FROM ExamQuestion eq WHERE eq.examId = :examId")
    BigDecimal calculateTotalScoreByExamId(@Param("examId") Long examId);
    
    /**
     * 获取考试中的最大题目顺序号
     */
    @Query("SELECT MAX(eq.questionOrder) FROM ExamQuestion eq WHERE eq.examId = :examId")
    Integer getMaxQuestionOrderByExamId(@Param("examId") Long examId);
    
    /**
     * 检查考试中是否存在指定题目
     */
    boolean existsByExamIdAndQuestionId(Long examId, Long questionId);
    
    /**
     * 检查考试中是否存在指定顺序的题目
     */
    boolean existsByExamIdAndQuestionOrder(Long examId, Integer questionOrder);
    
    /**
     * 根据考试ID删除所有题目关联
     */
    @Modifying
    @Query("DELETE FROM ExamQuestion eq WHERE eq.examId = :examId")
    int deleteByExamId(@Param("examId") Long examId);
    
    /**
     * 根据题目ID删除所有考试关联
     */
    @Modifying
    @Query("DELETE FROM ExamQuestion eq WHERE eq.questionId = :questionId")
    int deleteByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 删除指定考试的指定题目
     */
    @Modifying
    @Query("DELETE FROM ExamQuestion eq WHERE eq.examId = :examId AND eq.questionId = :questionId")
    int deleteByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);
    
    /**
     * 更新题目顺序
     */
    @Modifying
    @Query("UPDATE ExamQuestion eq SET eq.questionOrder = :newOrder WHERE eq.examId = :examId AND eq.questionId = :questionId")
    int updateQuestionOrder(@Param("examId") Long examId, 
                           @Param("questionId") Long questionId, 
                           @Param("newOrder") Integer newOrder);
    
    /**
     * 更新题目分数
     */
    @Modifying
    @Query("UPDATE ExamQuestion eq SET eq.score = :score WHERE eq.examId = :examId AND eq.questionId = :questionId")
    int updateQuestionScore(@Param("examId") Long examId, 
                           @Param("questionId") Long questionId, 
                           @Param("score") BigDecimal score);
    
    /**
     * 批量调整题目顺序（当删除题目后重新排序）
     */
    @Modifying
    @Query("UPDATE ExamQuestion eq SET eq.questionOrder = eq.questionOrder - 1 " +
           "WHERE eq.examId = :examId AND eq.questionOrder > :deletedOrder")
    int adjustQuestionOrderAfterDelete(@Param("examId") Long examId, 
                                      @Param("deletedOrder") Integer deletedOrder);
    
    /**
     * 获取考试中指定范围的题目
     */
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.examId = :examId " +
           "AND eq.questionOrder >= :startOrder AND eq.questionOrder <= :endOrder " +
           "ORDER BY eq.questionOrder")
    List<ExamQuestion> findByExamIdAndOrderRange(@Param("examId") Long examId,
                                                 @Param("startOrder") Integer startOrder,
                                                 @Param("endOrder") Integer endOrder);
    
    /**
     * 查找考试中分数最高的题目
     */
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.examId = :examId " +
           "AND eq.score = (SELECT MAX(eq2.score) FROM ExamQuestion eq2 WHERE eq2.examId = :examId)")
    List<ExamQuestion> findHighestScoreQuestionsByExamId(@Param("examId") Long examId);
    
    /**
     * 查找考试中分数最低的题目
     */
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.examId = :examId " +
           "AND eq.score = (SELECT MIN(eq2.score) FROM ExamQuestion eq2 WHERE eq2.examId = :examId)")
    List<ExamQuestion> findLowestScoreQuestionsByExamId(@Param("examId") Long examId);
}