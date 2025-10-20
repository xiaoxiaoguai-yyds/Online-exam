package org.bishe.question.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bishe.question.entity.Question;
import org.bishe.question.entity.Question.QuestionType;
import org.bishe.question.entity.Question.Difficulty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExcelImportService {

    @Autowired
    private QuestionService questionService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 从Excel文件导入题目数据
     */
    public Map<String, Object> importQuestionsFromExcel(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Question> questions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Question question = parseRowToQuestion(row, i + 1);
                    if (question != null) {
                        questions.add(question);
                        successCount++;
                    }
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行数据解析失败: " + e.getMessage());
                    errorCount++;
                }
            }
        }

        // 批量保存题目
        int actualSuccessCount = 0;
        int actualErrorCount = 0;
        
        if (!questions.isEmpty()) {
            for (Question question : questions) {
                try {
                    saveQuestion(question);
                    actualSuccessCount++;
                } catch (Exception e) {
                    errors.add("保存题目失败: " + question.getTitle() + " - " + e.getMessage());
                    actualErrorCount++;
                }
            }
        }
        
        // 更新最终统计结果
        successCount = actualSuccessCount;
        errorCount = errorCount + actualErrorCount;
        
        // 添加调试日志
        System.out.println("=== Excel导入结果统计 ===");
        System.out.println("解析成功: " + questions.size() + " 条");
        System.out.println("保存成功: " + successCount + " 条");
        System.out.println("保存失败: " + actualErrorCount + " 条");
        System.out.println("总错误数: " + errorCount + " 条");
        System.out.println("错误信息: " + errors);

        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        result.put("errors", errors);
        result.put("totalCount", successCount + errorCount);
        
        System.out.println("返回结果: " + result);
        return result;
    }
    
    /**
     * 保存题目实体
     */
    private void saveQuestion(Question question) {
        // 直接使用QuestionService的底层Repository保存
        // 这里需要调用QuestionService中的保存方法
        questionService.saveQuestion(question);
    }

    /**
     * 解析Excel行数据为Question对象
     */
    private Question parseRowToQuestion(Row row, int rowNum) {
        Question question = new Question();

        // 题目标题 (A列)
        String title = getCellStringValue(row.getCell(0));
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("题目标题不能为空");
        }
        question.setTitle(title.trim());

        // 题目内容 (B列)
        String content = getCellStringValue(row.getCell(1));
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("题目内容不能为空");
        }
        question.setContent(content.trim());

        // 题目类型 (C列)
        String typeStr = getCellStringValue(row.getCell(2));
        QuestionType type = parseQuestionType(typeStr);
        if (type == null) {
            throw new RuntimeException("无效的题目类型: " + typeStr);
        }
        question.setType(type);

        // 难度等级 (D列)
        String difficultyStr = getCellStringValue(row.getCell(3));
        Difficulty difficulty = parseDifficultyLevel(difficultyStr);
        if (difficulty == null) {
            throw new RuntimeException("无效的难度等级: " + difficultyStr);
        }
        question.setDifficulty(difficulty);

        // 选项 (E-H列)
        List<String> options = new ArrayList<>();
        for (int i = 4; i <= 7; i++) {
            String option = getCellStringValue(row.getCell(i));
            if (option != null && !option.trim().isEmpty()) {
                options.add(option.trim());
            }
        }
        
        // 单选题和多选题必须有选项
        if ((type == QuestionType.single || type == QuestionType.multiple) && options.isEmpty()) {
            throw new RuntimeException("选择题必须提供选项");
        }
        
        // 将选项列表转换为JSON字符串
        if (!options.isEmpty()) {
            try {
                question.setOptions(objectMapper.writeValueAsString(options));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("选项数据格式错误", e);
            }
        }

        // 正确答案 (I列)
        String correctAnswer = getCellStringValue(row.getCell(8));
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new RuntimeException("正确答案不能为空");
        }
        
        // 根据题目类型处理答案存储
        if (type == QuestionType.single || type == QuestionType.multiple) {
            // 选择题：将答案存储到correct_answers字段（JSON数组格式）
            List<String> correctAnswersList = new ArrayList<>();
            if (type == QuestionType.multiple) {
                // 多选题答案可能用逗号分隔
                String[] answers = correctAnswer.split(",|，");
                for (String answer : answers) {
                    if (!answer.trim().isEmpty()) {
                        correctAnswersList.add(answer.trim());
                    }
                }
            } else {
                // 单选题单个答案
                correctAnswersList.add(correctAnswer.trim());
            }
            
            try {
                question.setCorrectAnswers(objectMapper.writeValueAsString(correctAnswersList));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("正确答案数据格式错误", e);
            }
        } else {
            // 判断题、填空题、问答题：将答案存储到correct_answer字段（文本格式）
            question.setCorrectAnswer(correctAnswer.trim());
        }

        // 标签 (J列)
        String tagsStr = getCellStringValue(row.getCell(9));
        List<String> tags = new ArrayList<>();
        if (tagsStr != null && !tagsStr.trim().isEmpty()) {
            String[] tagArray = tagsStr.split(",|，");
            for (String tag : tagArray) {
                if (!tag.trim().isEmpty()) {
                    tags.add(tag.trim());
                }
            }
        }
        
        // 将标签列表转换为JSON字符串
        if (!tags.isEmpty()) {
            try {
                question.setTags(objectMapper.writeValueAsString(tags));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("标签数据格式错误", e);
            }
        }

        // 设置默认值
        question.setStatus(1); // 启用状态
        question.setCreatedTime(LocalDateTime.now());
        question.setUpdatedTime(LocalDateTime.now());

        return question;
    }

    /**
     * 获取单元格字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 解析题目类型
     */
    private QuestionType parseQuestionType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return null;
        }

        typeStr = typeStr.trim();
        switch (typeStr) {
            case "单选题":
            case "单选":
            case "single":
                return QuestionType.single;
            case "多选题":
            case "多选":
            case "multiple":
                return QuestionType.multiple;
            case "判断题":
            case "判断":
            case "judge":
                return QuestionType.judge;
            case "填空题":
            case "填空":
            case "fill":
                return QuestionType.fill;
            case "简答题":
            case "简答":
            case "问答题":
            case "essay":
                return QuestionType.essay;
            default:
                return null;
        }
    }

    /**
     * 解析难度等级
     */
    private Difficulty parseDifficultyLevel(String difficultyStr) {
        if (difficultyStr == null || difficultyStr.trim().isEmpty()) {
            return null;
        }

        difficultyStr = difficultyStr.trim();
        switch (difficultyStr) {
            case "简单":
            case "容易":
            case "easy":
                return Difficulty.easy;
            case "中等":
            case "普通":
            case "medium":
                return Difficulty.medium;
            case "困难":
            case "难":
            case "hard":
                return Difficulty.hard;
            default:
                return null;
        }
    }

    /**
     * 生成Excel模板
     */
    public Workbook generateTemplate() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("题目导入模板");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "题目标题*", "题目内容*", "题目类型*", "难度等级*", 
            "选项A", "选项B", "选项C", "选项D", 
            "正确答案*", "标签(多个用逗号分隔)"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 创建示例数据行 - 单选题
        Row example1 = sheet.createRow(1);
        String[] singleChoiceData = {
            "Java中String类的特点",
            "以下关于Java中String类的描述，哪个是正确的？",
            "单选题",
            "中等",
            "String是可变的",
            "String是不可变的",
            "String可以被继承",
            "String不是线程安全的",
            "B",
            "Java基础,String类"
        };
        for (int i = 0; i < singleChoiceData.length; i++) {
            Cell cell = example1.createCell(i);
            cell.setCellValue(singleChoiceData[i]);
        }

        // 创建示例数据行 - 多选题
        Row example2 = sheet.createRow(2);
        String[] multipleChoiceData = {
            "Java集合框架的特点",
            "以下哪些是Java集合框架的特点？（多选）",
            "多选题",
            "困难",
            "支持泛型",
            "线程安全",
            "动态扩容",
            "统一接口",
            "A,C,D",
            "Java集合,框架特性"
        };
        for (int i = 0; i < multipleChoiceData.length; i++) {
            Cell cell = example2.createCell(i);
            cell.setCellValue(multipleChoiceData[i]);
        }

        // 创建示例数据行 - 判断题
        Row example3 = sheet.createRow(3);
        String[] judgeData = {
            "Java是面向对象语言",
            "Java是一种纯面向对象的编程语言。",
            "判断题",
            "简单",
            "",
            "",
            "",
            "",
            "正确",
            "Java基础,面向对象"
        };
        for (int i = 0; i < judgeData.length; i++) {
            Cell cell = example3.createCell(i);
            cell.setCellValue(judgeData[i]);
        }

        // 创建示例数据行 - 填空题
        Row example4 = sheet.createRow(4);
        String[] fillData = {
            "Java访问修饰符",
            "Java中有四种访问修饰符：______、______、______、______。",
            "填空题",
            "中等",
            "",
            "",
            "",
            "",
            "public,protected,default,private",
            "Java基础,访问修饰符"
        };
        for (int i = 0; i < fillData.length; i++) {
            Cell cell = example4.createCell(i);
            cell.setCellValue(fillData[i]);
        }

        // 创建示例数据行 - 问答题
        Row example5 = sheet.createRow(5);
        String[] essayData = {
            "面向对象三大特性",
            "请简述面向对象编程的三大特性及其作用。",
            "问答题",
            "困难",
            "",
            "",
            "",
            "",
            "封装、继承、多态。封装隐藏内部实现细节；继承实现代码复用；多态提供统一接口。",
            "面向对象,编程思想"
        };
        for (int i = 0; i < essayData.length; i++) {
            Cell cell = example5.createCell(i);
            cell.setCellValue(essayData[i]);
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}