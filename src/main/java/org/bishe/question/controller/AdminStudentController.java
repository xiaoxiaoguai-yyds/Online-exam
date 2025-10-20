package org.bishe.question.controller;

import org.bishe.question.dto.ApiResponse;
import org.bishe.question.entity.Student;
import org.bishe.question.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员学生管理控制器
 */
@RestController
@RequestMapping("/admin/students")
@CrossOrigin(origins = "*")
public class AdminStudentController {
    
    @Autowired
    private StudentService studentService;
    
    /**
     * 分页查询所有学生
     */
    @GetMapping
    public ApiResponse<Page<Student>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Student> students = studentService.findAllStudents(page, size, sortBy, sortDir);
            return ApiResponse.success("获取学生列表成功", students);
        } catch (Exception e) {
            return ApiResponse.error("获取学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索学生
     */
    @GetMapping("/search")
    public ApiResponse<Page<Student>> searchStudents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Student> students = studentService.searchStudents(keyword, page, size, sortBy, sortDir);
            return ApiResponse.success("搜索学生成功", students);
        } catch (Exception e) {
            return ApiResponse.error("搜索学生失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据状态查询学生
     */
    @GetMapping("/status/{status}")
    public ApiResponse<Page<Student>> getStudentsByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Student> students = studentService.findStudentsByStatus(status, page, size, sortBy, sortDir);
            return ApiResponse.success("获取学生列表成功", students);
        } catch (Exception e) {
            return ApiResponse.error("获取学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据班级查询学生
     */
    @GetMapping("/class/{className}")
    public ApiResponse<Page<Student>> getStudentsByClass(
            @PathVariable String className,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Student> students = studentService.findStudentsByClassName(className, page, size, sortBy, sortDir);
            return ApiResponse.success("获取班级学生列表成功", students);
        } catch (Exception e) {
            return ApiResponse.error("获取班级学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据专业查询学生
     */
    @GetMapping("/major/{major}")
    public ApiResponse<Page<Student>> getStudentsByMajor(
            @PathVariable String major,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<Student> students = studentService.findStudentsByMajor(major, page, size, sortBy, sortDir);
            return ApiResponse.success("获取专业学生列表成功", students);
        } catch (Exception e) {
            return ApiResponse.error("获取专业学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取学生详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Student> getStudentById(@PathVariable Long id) {
        try {
            Student student = studentService.findById(id)
                    .orElseThrow(() -> new RuntimeException("学生不存在"));
            return ApiResponse.success("获取学生详情成功", student);
        } catch (Exception e) {
            return ApiResponse.error("获取学生详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新学生状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Student> updateStudentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            if (status == null) {
                return ApiResponse.error("状态参数不能为空");
            }
            Student student = studentService.updateStudentStatus(id, status);
            return ApiResponse.success("更新学生状态成功", student);
        } catch (Exception e) {
            return ApiResponse.error("更新学生状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ApiResponse.success("删除学生成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除学生失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteStudents(@RequestBody List<Long> studentIds) {
        try {
            studentService.deleteStudents(studentIds);
            return ApiResponse.success("批量删除学生成功", null);
        } catch (Exception e) {
            return ApiResponse.error("批量删除学生失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学生统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<StudentService.StudentStatistics> getStudentStatistics() {
        try {
            StudentService.StudentStatistics statistics = studentService.getStudentStatistics();
            return ApiResponse.success("获取学生统计信息成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取学生统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有班级列表
     */
    @GetMapping("/classes")
    public ApiResponse<List<String>> getAllClasses() {
        try {
            List<String> classes = studentService.findDistinctClassNames();
            return ApiResponse.success("获取班级列表成功", classes);
        } catch (Exception e) {
            return ApiResponse.error("获取班级列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有专业列表
     */
    @GetMapping("/majors")
    public ApiResponse<List<String>> getAllMajors() {
        try {
            List<String> majors = studentService.findDistinctMajors();
            return ApiResponse.success("获取专业列表成功", majors);
        } catch (Exception e) {
            return ApiResponse.error("获取专业列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有年级列表
     */
    @GetMapping("/grades")
    public ApiResponse<List<String>> getAllGrades() {
        try {
            List<String> grades = studentService.findDistinctGrades();
            return ApiResponse.success("获取年级列表成功", grades);
        } catch (Exception e) {
            return ApiResponse.error("获取年级列表失败: " + e.getMessage());
        }
    }
}