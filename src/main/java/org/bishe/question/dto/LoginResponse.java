package org.bishe.question.dto;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 */
public class LoginResponse {
    
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private String token;
    private LocalDateTime loginTime;
    
    public LoginResponse() {}
    
    public LoginResponse(Long userId, String username, String nickname, String email) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.loginTime = LocalDateTime.now();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
        return "LoginResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", loginTime=" + loginTime +
                '}';
    }
}