package com.jobportal.dto;

import java.time.LocalDateTime;

public class ApplicationResponse {
    private Long id, jobId, userId;
    private String jobTitle, company, userName, userEmail, resumeFileName;
    private Double matchScore;
    private String matchedSkills, missingSkills, recommendations, status;
    private LocalDateTime appliedAt;

    public ApplicationResponse() {}
    public ApplicationResponse(Long id, Long jobId, String jobTitle, String company, Long userId,
                                String userName, String userEmail, String resumeFileName, Double matchScore,
                                String matchedSkills, String missingSkills, String recommendations,
                                String status, LocalDateTime appliedAt) {
        this.id=id; this.jobId=jobId; this.jobTitle=jobTitle; this.company=company; this.userId=userId;
        this.userName=userName; this.userEmail=userEmail; this.resumeFileName=resumeFileName;
        this.matchScore=matchScore; this.matchedSkills=matchedSkills; this.missingSkills=missingSkills;
        this.recommendations=recommendations; this.status=status; this.appliedAt=appliedAt;
    }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getJobId() { return jobId; } public void setJobId(Long id) { this.jobId = id; }
    public String getJobTitle() { return jobTitle; } public void setJobTitle(String s) { this.jobTitle = s; }
    public String getCompany() { return company; } public void setCompany(String s) { this.company = s; }
    public Long getUserId() { return userId; } public void setUserId(Long id) { this.userId = id; }
    public String getUserName() { return userName; } public void setUserName(String s) { this.userName = s; }
    public String getUserEmail() { return userEmail; } public void setUserEmail(String s) { this.userEmail = s; }
    public String getResumeFileName() { return resumeFileName; } public void setResumeFileName(String s) { this.resumeFileName = s; }
    public Double getMatchScore() { return matchScore; } public void setMatchScore(Double s) { this.matchScore = s; }
    public String getMatchedSkills() { return matchedSkills; } public void setMatchedSkills(String s) { this.matchedSkills = s; }
    public String getMissingSkills() { return missingSkills; } public void setMissingSkills(String s) { this.missingSkills = s; }
    public String getRecommendations() { return recommendations; } public void setRecommendations(String s) { this.recommendations = s; }
    public String getStatus() { return status; } public void setStatus(String s) { this.status = s; }
    public LocalDateTime getAppliedAt() { return appliedAt; } public void setAppliedAt(LocalDateTime t) { this.appliedAt = t; }
}
