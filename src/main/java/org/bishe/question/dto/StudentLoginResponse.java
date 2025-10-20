package org.bishe.question.dto;

import java.time.LocalDateTime;

/**
 * 学生登录响应DTO
 */
public class StudentLoginResponse {
    
    private Long id;
    private String studentNumber;
    private String name;
    private String email;
    private String phone;
    private String className;
    private String major;
    private String grade;
    private String token;
    private LocalDateTime loginTime;
    
    public StudentLoginResponse() {}
    
    public StudentLoginResponse(Long id, String studentNumber, String name, String token) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.name = name;
        this.token = token;
        this.loginTime = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
    
    @Override
    public String toString() {
        return "StudentLoginResponse{" +
                "id=" + id +
                ", studentNumber='" + studentNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", className='" + className + '\'' +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", token='[PROTECTED]'" +
                ", loginTime=" + loginTime +
                '}';
    }
}