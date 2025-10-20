package org.bishe.question.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 学生登录请求DTO
 */
public class StudentLoginRequest {
    
    @NotBlank(message = "学号不能为空")
    private String studentNumber;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    public StudentLoginRequest() {}
    
    public StudentLoginRequest(String studentNumber, String password) {
        this.studentNumber = studentNumber;
        this.password = password;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "StudentLoginRequest{" +
                "studentNumber='" + studentNumber + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}