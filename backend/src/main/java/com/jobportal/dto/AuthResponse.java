package com.jobportal.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String fullName;
    private String email;
    private String role;

    public AuthResponse() {}
    public AuthResponse(String token, String type, Long userId, String fullName, String email, String role) {
        this.token = token; this.type = type; this.userId = userId;
        this.fullName = fullName; this.email = email; this.role = role;
    }

    public String getToken() { return token; } public void setToken(String s) { this.token = s; }
    public String getType() { return type; } public void setType(String s) { this.type = s; }
    public Long getUserId() { return userId; } public void setUserId(Long id) { this.userId = id; }
    public String getFullName() { return fullName; } public void setFullName(String s) { this.fullName = s; }
    public String getEmail() { return email; } public void setEmail(String s) { this.email = s; }
    public String getRole() { return role; } public void setRole(String s) { this.role = s; }
}
