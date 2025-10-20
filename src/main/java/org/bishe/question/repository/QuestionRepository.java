package org.bishe.question.repository;

import org.bishe.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目数据访问层
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 根据题目类型查找题目
     * @param type 题目类型
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    Page<Question> findByType(Question.QuestionType type, Pageable pageable);
    
    /**
     * 根据难度等级查找题目
     * @param difficulty 难度等级
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    Page<Question> findByDifficulty(Question.Difficulty difficulty, Pageable pageable);
    
    /**
     * 根据题目类型和难度等级查找题目
     * @param type 题目类型
     * @param difficulty 难度等级
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    Page<Question> findByTypeAndDifficulty(Question.QuestionType type, Question.Difficulty difficulty, Pageable pageable);
    
    /**
     * 根据状态查找题目
     * @param status 状态
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    Page<Question> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据创建者查找题目
     * @param createdBy 创建者ID
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    Page<Question> findByCreatedBy(Long createdBy, Pageable pageable);
    
    /**
     * 搜索题目（根据标题和内容）
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据标题、内容和标签搜索题目
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword% OR q.tags LIKE %:keyword%")
    Page<Question> searchByKeywordIncludingTags(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 复合搜索：根据关键词、类型和难度查找题目
     * @param keyword 关键词
     * @param type 题目类型
     * @param difficulty 难度等级
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    @Query("SELECT q FROM Question q WHERE " +
           "(:keyword IS NULL OR q.title LIKE %:keyword% OR q.content LIKE %:keyword% OR q.tags LIKE %:keyword%) AND " +
           "(:type IS NULL OR q.type = :type) AND " +
           "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
           "q.status = 1")
    Page<Question> searchQuestions(@Param("keyword") String keyword, 
                                  @Param("type") Question.QuestionType type, 
                                  @Param("difficulty") Question.Difficulty difficulty, 
                                  Pageable pageable);
    
    /**
     * 统计各类型题目数量
     * @return 统计结果
     */
    @Query("SELECT q.type, COUNT(q) FROM Question q WHERE q.status = 1 GROUP BY q.type")
    List<Object[]> countByType();
    
    /**
     * 统计各难度题目数量
     * @return 统计结果
     */
    @Query("SELECT q.difficulty, COUNT(q) FROM Question q WHERE q.status = 1 GROUP BY q.difficulty")
    List<Object[]> countByDifficulty();
    
    /**
     * 获取最近创建的题目
     * @param limit 限制数量
     * @return 题目列表
     */
    @Query("SELECT q FROM Question q WHERE q.status = 1 ORDER BY q.createdTime DESC")
    List<Question> findRecentQuestions(Pageable pageable);
    
    /**
     * 根据标签查找题目
     * @param tag 标签
     * @param pageable 分页参数
     * @return 题目分页列表
     */
    @Query("SELECT q FROM Question q WHERE q.tags LIKE %:tag% AND q.status = 1")
    Page<Question> findByTag(@Param("tag") String tag, Pageable pageable);
    
    /**
     * 检查题目标题是否存在
     * @param title 题目标题
     * @return 是否存在
     */
    boolean existsByTitle(String title);
    
    /**
     * 检查题目标题是否已存在（排除指定ID）
     * @param title 题目标题
     * @param id 要排除的题目ID
     * @return 是否存在
     */
    boolean existsByTitleAndIdNot(String title, Long id);
    
    /**
     * 根据状态查找题目（按ID升序）
     * @param status 状态
     * @return 题目列表
     */
    List<Question> findByStatusOrderByIdAsc(Integer status);
    
    /**
     * 根据类型统计题目数量
     * @param type 题目类型
     * @return 题目数量
     */
    Long countByType(Question.QuestionType type);
    
    /**
     * 根据难度统计题目数量
     * @param difficulty 难度
     * @return 题目数量
     */
    Long countByDifficulty(Question.Difficulty difficulty);
}