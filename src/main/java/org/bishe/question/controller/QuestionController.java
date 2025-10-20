package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.dto.QuestionRequest;
import org.bishe.question.dto.QuestionResponse;
import org.bishe.question.entity.Question;
import org.bishe.question.service.QuestionService;
import org.bishe.question.service.ExcelExportService;
import org.bishe.question.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 题目管理控制器
 */
@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private ExcelImportService excelImportService;
    
    /**
     * 创建题目
     * @param questionRequest 题目请求
     * @return API响应
     */
    @PostMapping
    public ApiResponse<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        System.out.println("=== 创建题目API调用 ===");
        System.out.println("请求数据: " + questionRequest);
        
        try {
            // 这里暂时使用固定的用户ID，实际应该从认证信息中获取
            Long createdBy = 1L;
            QuestionResponse response = questionService.createQuestion(questionRequest, createdBy);
            
            System.out.println("题目创建成功: " + response.getId());
            return ApiResponse.success("题目创建成功", response);
        } catch (Exception e) {
            System.err.println("创建题目失败: " + e.getMessage());
            return ApiResponse.error("创建题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新题目
     * @param id 题目ID
     * @param questionRequest 题目请求
     * @return API响应
     */
    @PutMapping("/{id}")
    public ApiResponse<QuestionResponse> updateQuestion(@PathVariable Long id, 
                                                       @Valid @RequestBody QuestionRequest questionRequest) {
        System.out.println("=== 更新题目API调用 ===");
        System.out.println("题目ID: " + id);
        System.out.println("请求数据: " + questionRequest);
        
        try {
            QuestionResponse response = questionService.updateQuestion(id, questionRequest);
            
            System.out.println("题目更新成功");
            return ApiResponse.success("题目更新成功", response);
        } catch (Exception e) {
            System.err.println("更新题目失败: " + e.getMessage());
            return ApiResponse.error("更新题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除题目
     * @param id 题目ID
     * @return API响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        System.out.println("=== 删除题目API调用 ===");
        System.out.println("题目ID: " + id);
        
        try {
            questionService.deleteQuestion(id);
            
            System.out.println("题目删除成功");
            return ApiResponse.success();
        } catch (Exception e) {
            System.err.println("删除题目失败: " + e.getMessage());
            return ApiResponse.error("删除题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取题目
     * @param id 题目ID
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<QuestionResponse> getQuestionById(@PathVariable Long id) {
        System.out.println("=== 获取题目详情API调用 ===");
        System.out.println("题目ID: " + id);
        
        try {
            QuestionResponse response = questionService.getQuestionById(id);
            
            System.out.println("获取题目详情成功");
            return ApiResponse.success("获取题目详情成功", response);
        } catch (Exception e) {
            System.err.println("获取题目详情失败: " + e.getMessage());
            return ApiResponse.error("获取题目详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询题目
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param type 题目类型
     * @param difficulty 难度等级
     * @return API响应
     */
    @GetMapping
    public ApiResponse<Page<QuestionResponse>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Question.QuestionType type,
            @RequestParam(required = false) Question.Difficulty difficulty) {
        
        System.out.println("=== 分页查询题目API调用 ===");
        System.out.println("页码: " + page + ", 大小: " + size);
        System.out.println("关键词: " + keyword + ", 类型: " + type + ", 难度: " + difficulty);
        
        try {
            Page<QuestionResponse> response = questionService.getQuestions(page, size, keyword, type, difficulty);
            
            System.out.println("查询题目成功，总数: " + response.getTotalElements());
            return ApiResponse.success("查询题目成功", response);
        } catch (Exception e) {
            System.err.println("查询题目失败: " + e.getMessage());
            return ApiResponse.error("查询题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取题目统计信息
     * @return API响应
     */
    @GetMapping("/stats")
    public ApiResponse<Object> getQuestionStats() {
        System.out.println("=== 获取题目统计信息API调用 ===");
        
        try {
            Object stats = questionService.getQuestionStats();
            
            System.out.println("获取统计信息成功");
            return ApiResponse.success("获取统计信息成功", stats);
        } catch (Exception e) {
            System.err.println("获取统计信息失败: " + e.getMessage());
            return ApiResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出题目为Excel
     * @param keyword 搜索关键词
     * @param type 题目类型
     * @param difficulty 难度等级
     * @return Excel文件
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportQuestions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Question.QuestionType type,
            @RequestParam(required = false) Question.Difficulty difficulty) {
        try {
            // 获取所有符合条件的题目（用于导出）
            List<Question> questions = questionService.getQuestionsForExport(keyword, type, difficulty);
            
            // 导出为Excel
            byte[] excelData = excelExportService.exportQuestionsToExcel(questions);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "questions.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            System.err.println("导出Excel失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 健康检查接口
     * @return API响应
     */
    /**
     * 下载Excel导入模板
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            Workbook workbook = excelImportService.generateTemplate();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            byte[] bytes = outputStream.toByteArray();
            outputStream.close();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "question_import_template.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 导入Excel文件中的题目数据
     */
    @PostMapping("/import")
    public ApiResponse<Map<String, Object>> importQuestions(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件类型
            if (file.isEmpty()) {
                return ApiResponse.error("请选择要上传的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return ApiResponse.error("请上传Excel文件(.xlsx或.xls格式)");
            }
            
            // 导入数据
            Map<String, Object> result = excelImportService.importQuestionsFromExcel(file);
            
            return ApiResponse.success("导入完成", result);
        } catch (IOException e) {
            return ApiResponse.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("题目管理服务运行正常", "OK");
    }
}