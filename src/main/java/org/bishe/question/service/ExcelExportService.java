package org.bishe.question.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bishe.question.entity.Question;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Excel导出服务
 */
@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出题目列表为Excel
     * @param questions 题目列表
     * @return Excel文件的字节数组
     * @throws IOException IO异常
     */
    public byte[] exportQuestionsToExcel(List<Question> questions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("题目列表");
            
            // 创建标题行
            createHeaderRow(sheet);
            
            // 创建数据行
            int rowNum = 1;
            for (Question question : questions) {
                createDataRow(sheet, question, rowNum++);
            }
            
            // 自动调整列宽
            autoSizeColumns(sheet);
            
            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 创建表头行
     */
    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        
        // 创建表头样式
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        
        // 定义表头
        String[] headers = {"ID", "题目标题", "题目内容", "题目类型", "难度等级", "选项", "正确答案", "标签", "状态", "创建时间", "更新时间"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 创建数据行
     */
    private void createDataRow(Sheet sheet, Question question, int rowNum) {
        Row row = sheet.createRow(rowNum);
        
        // 创建数据样式
        CellStyle dataStyle = sheet.getWorkbook().createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setWrapText(true); // 自动换行
        
        int colNum = 0;
        
        // ID
        Cell idCell = row.createCell(colNum++);
        idCell.setCellValue(question.getId());
        idCell.setCellStyle(dataStyle);
        
        // 题目标题
        Cell titleCell = row.createCell(colNum++);
        titleCell.setCellValue(question.getTitle());
        titleCell.setCellStyle(dataStyle);
        
        // 题目内容
        Cell contentCell = row.createCell(colNum++);
        contentCell.setCellValue(question.getContent());
        contentCell.setCellStyle(dataStyle);
        
        // 题目类型
        Cell typeCell = row.createCell(colNum++);
        typeCell.setCellValue(getTypeDisplayName(question.getType()));
        typeCell.setCellStyle(dataStyle);
        
        // 难度等级
        Cell difficultyCell = row.createCell(colNum++);
        difficultyCell.setCellValue(getDifficultyDisplayName(question.getDifficulty()));
        difficultyCell.setCellStyle(dataStyle);
        
        // 选项
        Cell optionsCell = row.createCell(colNum++);
        optionsCell.setCellValue(question.getOptions() != null ? question.getOptions() : "");
        optionsCell.setCellStyle(dataStyle);
        
        // 正确答案
        Cell answerCell = row.createCell(colNum++);
        String correctAnswer = "";
        if (question.getCorrectAnswers() != null) {
            correctAnswer = question.getCorrectAnswers();
        } else if (question.getCorrectAnswer() != null) {
            correctAnswer = question.getCorrectAnswer();
        }
        answerCell.setCellValue(correctAnswer);
        answerCell.setCellStyle(dataStyle);
        
        // 标签
        Cell tagsCell = row.createCell(colNum++);
        tagsCell.setCellValue(question.getTags() != null ? question.getTags() : "");
        tagsCell.setCellStyle(dataStyle);
        
        // 状态
        Cell statusCell = row.createCell(colNum++);
        statusCell.setCellValue(question.getStatus() == 1 ? "启用" : "禁用");
        statusCell.setCellStyle(dataStyle);
        
        // 创建时间
        Cell createTimeCell = row.createCell(colNum++);
        createTimeCell.setCellValue(question.getCreatedTime() != null ? 
            question.getCreatedTime().format(DATE_FORMATTER) : "");
        createTimeCell.setCellStyle(dataStyle);
        
        // 更新时间
        Cell updateTimeCell = row.createCell(colNum++);
        updateTimeCell.setCellValue(question.getUpdatedTime() != null ? 
            question.getUpdatedTime().format(DATE_FORMATTER) : "");
        updateTimeCell.setCellStyle(dataStyle);
    }

    /**
     * 自动调整列宽
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 11; i++) {
            sheet.autoSizeColumn(i);
            // 设置最大列宽，避免内容过长导致列宽过大
            int columnWidth = sheet.getColumnWidth(i);
            if (columnWidth > 15000) {
                sheet.setColumnWidth(i, 15000);
            }
        }
    }

    /**
     * 获取题目类型显示名称
     */
    private String getTypeDisplayName(Question.QuestionType type) {
        if (type == null) return "";
        switch (type) {
            case single: return "单选题";
            case multiple: return "多选题";
            case judge: return "判断题";
            case fill: return "填空题";
            case essay: return "问答题";
            default: return type.toString();
        }
    }

    /**
     * 获取难度等级显示名称
     */
    private String getDifficultyDisplayName(Question.Difficulty difficulty) {
        if (difficulty == null) return "";
        switch (difficulty) {
            case easy: return "简单";
            case medium: return "中等";
            case hard: return "困难";
            default: return difficulty.toString();
        }
    }
}