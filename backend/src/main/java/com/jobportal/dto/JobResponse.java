package com.jobportal.dto;

import java.time.LocalDateTime;

public class JobResponse {
    private Long id;
    private String title, company, location, description, requiredSkills, salaryRange, jobType, postedByName;
    private LocalDateTime createdAt, updatedAt;
    private int applicantCount;

    public JobResponse() {}
    public JobResponse(Long id, String title, String company, String location, String description,
                       String requiredSkills, String salaryRange, String jobType, String postedByName,
                       LocalDateTime createdAt, LocalDateTime updatedAt, int applicantCount) {
        this.id=id; this.title=title; this.company=company; this.location=location; this.description=description;
        this.requiredSkills=requiredSkills; this.salaryRange=salaryRange; this.jobType=jobType;
        this.postedByName=postedByName; this.createdAt=createdAt; this.updatedAt=updatedAt; this.applicantCount=applicantCount;
    }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String s) { this.title = s; }
    public String getCompany() { return company; } public void setCompany(String s) { this.company = s; }
    public String getLocation() { return location; } public void setLocation(String s) { this.location = s; }
    public String getDescription() { return description; } public void setDescription(String s) { this.description = s; }
    public String getRequiredSkills() { return requiredSkills; } public void setRequiredSkills(String s) { this.requiredSkills = s; }
    public String getSalaryRange() { return salaryRange; } public void setSalaryRange(String s) { this.salaryRange = s; }
    public String getJobType() { return jobType; } public void setJobType(String s) { this.jobType = s; }
    public String getPostedByName() { return postedByName; } public void setPostedByName(String s) { this.postedByName = s; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
    public int getApplicantCount() { return applicantCount; } public void setApplicantCount(int c) { this.applicantCount = c; }
}
