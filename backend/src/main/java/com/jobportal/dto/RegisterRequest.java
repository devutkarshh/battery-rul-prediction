package com.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Full name is required") @Size(min = 2, max = 100)
    private String fullName;
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required") @Size(min = 6, max = 100)
    private String password;

    public String getFullName() { return fullName; } public void setFullName(String s) { this.fullName = s; }
    public String getEmail() { return email; } public void setEmail(String s) { this.email = s; }
    public String getPassword() { return password; } public void setPassword(String s) { this.password = s; }
}
