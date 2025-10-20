package org.bishe.question.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 学生答题记录实体类
 */
@Entity
@Table(name = "student_answers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StudentAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_record_id", nullable = false)
    private Long examRecordId;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "student_answer", columnDefinition = "TEXT")
    private String studentAnswer;
    
    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "answer_time")
    private LocalDateTime answerTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_record_id", insertable = false, updatable = false)
    private ExamRecord examRecord;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public StudentAnswer() {}
    
    public StudentAnswer(Long examRecordId, Long questionId, String studentAnswer) {
        this.examRecordId = examRecordId;
        this.questionId = questionId;
        this.studentAnswer = studentAnswer;
        this.answerTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getExamRecordId() {
        return examRecordId;
    }
    
    public void setExamRecordId(Long examRecordId) {
        this.examRecordId = examRecordId;
    }
    
    public Long getExamId() {
        return examId;
    }
    
    public void setExamId(Long examId) {
        this.examId = examId;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getStudentAnswer() {
        return studentAnswer;
    }
    
    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
        this.answerTime = LocalDateTime.now();
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public Boolean getIsCorrect() {
        return isCorrect;
    }
    
    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    
    public LocalDateTime getAnswerTime() {
        return answerTime;
    }
    
    public void setAnswerTime(LocalDateTime answerTime) {
        this.answerTime = answerTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public ExamRecord getExamRecord() {
        return examRecord;
    }
    
    public void setExamRecord(ExamRecord examRecord) {
        this.examRecord = examRecord;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    // 业务方法
    
    /**
     * 检查答案是否正确
     * 需要根据题目类型进行不同的比对逻辑
     */
    public void checkAnswer() {
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            this.isCorrect = false;
            return;
        }
        
        // 获取关联的题目信息来判断题目类型
        if (question != null) {
            checkAnswerByQuestionType();
        } else {
            // 如果没有关联题目信息，使用传统的字符串比较（兼容填空题和问答题）
            if (correctAnswer == null) {
                this.isCorrect = false;
                return;
            }
            
            String studentAns = studentAnswer.trim().toLowerCase();
            String correctAns = correctAnswer.trim().toLowerCase();
            this.isCorrect = studentAns.equals(correctAns);
        }
    }
    
    /**
     * 根据题目类型检查答案
     */
    private void checkAnswerByQuestionType() {
        Question.QuestionType type = question.getType();
        
        switch (type) {
            case single:
                checkSingleChoiceAnswer();
                break;
            case multiple:
                checkMultipleChoiceAnswer();
                break;
            case judge:
                checkJudgeAnswer();
                break;
            case fill:
            case essay:
                checkTextAnswer();
                break;
            default:
                this.isCorrect = false;
        }
    }
    
    /**
     * 检查单选题答案
     */
    private void checkSingleChoiceAnswer() {
        try {
            String correctAnswersJson = question.getCorrectAnswers();
            if (correctAnswersJson == null) {
                this.isCorrect = false;
                return;
            }
            
            // 解析正确答案JSON数组，如["B"]
            String correctAnswer = correctAnswersJson.replaceAll("[\\[\\]\"]", "").trim();
            
            // 学生答案可能是索引(如"1")或选项字母(如"B")
            String studentAns = studentAnswer.trim();
            
            // 如果学生答案是数字索引，转换为选项字母
            if (studentAns.matches("\\\\d+")) {
                int index = Integer.parseInt(studentAns);
                if (index >= 0 && index < 26) {
                    studentAns = String.valueOf((char)('A' + index));
                }
            }
            
            this.isCorrect = correctAnswer.equalsIgnoreCase(studentAns);
        } catch (Exception e) {
            this.isCorrect = false;
        }
    }
    
    /**
     * 检查多选题答案
     */
    private void checkMultipleChoiceAnswer() {
        try {
            String correctAnswersJson = question.getCorrectAnswers();
            if (correctAnswersJson == null) {
                this.isCorrect = false;
                return;
            }
            
            // 解析正确答案JSON数组，如["A", "C", "D"]
            String[] correctAnswers = correctAnswersJson.replaceAll("[\\[\\]\"]", "")
                    .split(",");
            
            // 学生答案可能是JSON数组格式如"[1,2]"或逗号分隔如"A,C,D"
            String studentAns = studentAnswer.trim();
            
            // 处理JSON数组格式的学生答案
            if (studentAns.startsWith("[") && studentAns.endsWith("]")) {
                studentAns = studentAns.substring(1, studentAns.length() - 1);
            }
            
            String[] studentAnswers = studentAns.split(",");
            
            // 转换学生答案中的数字索引为选项字母
            for (int i = 0; i < studentAnswers.length; i++) {
                String ans = studentAnswers[i].trim();
                if (ans.matches("\\\\d+")) {
                    int index = Integer.parseInt(ans);
                    if (index >= 0 && index < 26) {
                        studentAnswers[i] = String.valueOf((char)('A' + index));
                    }
                } else {
                    studentAnswers[i] = ans.toUpperCase();
                }
            }
            
            // 比较答案数量
            if (correctAnswers.length != studentAnswers.length) {
                this.isCorrect = false;
                return;
            }
            
            // 检查每个正确答案是否都在学生答案中
            for (String correct : correctAnswers) {
                boolean found = false;
                for (String student : studentAnswers) {
                    if (correct.trim().equalsIgnoreCase(student.trim())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.isCorrect = false;
                    return;
                }
            }
            
            this.isCorrect = true;
        } catch (Exception e) {
            this.isCorrect = false;
        }
    }
    
    /**
     * 检查判断题答案
     */
    private void checkJudgeAnswer() {
        if (correctAnswer == null) {
            this.isCorrect = false;
            return;
        }
        
        String studentAns = studentAnswer.trim().toLowerCase();
        String correctAns = correctAnswer.trim().toLowerCase();
        
        // 处理不同的判断题答案格式
        // 学生答案可能是: true/false, 正确/错误, 对/错, T/F
        if (correctAns.equals("正确") || correctAns.equals("true") || correctAns.equals("对") || correctAns.equals("t")) {
            this.isCorrect = studentAns.equals("true") || studentAns.equals("正确") || 
                           studentAns.equals("对") || studentAns.equals("t");
        } else if (correctAns.equals("错误") || correctAns.equals("false") || correctAns.equals("错") || correctAns.equals("f")) {
            this.isCorrect = studentAns.equals("false") || studentAns.equals("错误") || 
                           studentAns.equals("错") || studentAns.equals("f");
        } else {
            this.isCorrect = correctAns.equals(studentAns);
        }
    }
    
    /**
     * 检查填空题和问答题答案
     */
    private void checkTextAnswer() {
        if (correctAnswer == null) {
            this.isCorrect = false;
            return;
        }
        
        String studentAns = studentAnswer.trim().toLowerCase();
        String correctAns = correctAnswer.trim().toLowerCase();
        
        // 对于填空题，可能有多个正确答案用逗号分隔
        if (correctAns.contains(",")) {
            String[] correctAnswers = correctAns.split(",");
            for (String correct : correctAnswers) {
                if (studentAns.equals(correct.trim())) {
                    this.isCorrect = true;
                    return;
                }
            }
            this.isCorrect = false;
        } else {
            this.isCorrect = studentAns.equals(correctAns);
        }
    }
    
    /**
     * 检查是否已回答
     */
    public boolean isAnswered() {
        return studentAnswer != null && !studentAnswer.trim().isEmpty();
    }
    
    /**
     * 清空答案
     */
    public void clearAnswer() {
        this.studentAnswer = null;
        this.isCorrect = null;
        this.answerTime = null;
    }
    
    @Override
    public String toString() {
        return "StudentAnswer{" +
                "id=" + id +
                ", examRecordId=" + examRecordId +
                ", questionId=" + questionId +
                ", studentAnswer='" + studentAnswer + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", isCorrect=" + isCorrect +
                ", answerTime=" + answerTime +
                '}';
    }
}