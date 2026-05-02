package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobRequest {
    @NotBlank(message = "Job title is required") @Size(max = 200) private String title;
    @NotBlank(message = "Company name is required") @Size(max = 200) private String company;
    @Size(max = 150) private String location;
    @NotBlank(message = "Job description is required") private String description;
    @NotBlank(message = "Required skills are required") private String requiredSkills;
    @Size(max = 100) private String salaryRange;
    private String jobType = "FULL_TIME";

    public String getTitle() { return title; } public void setTitle(String s) { this.title = s; }
    public String getCompany() { return company; } public void setCompany(String s) { this.company = s; }
    public String getLocation() { return location; } public void setLocation(String s) { this.location = s; }
    public String getDescription() { return description; } public void setDescription(String s) { this.description = s; }
    public String getRequiredSkills() { return requiredSkills; } public void setRequiredSkills(String s) { this.requiredSkills = s; }
    public String getSalaryRange() { return salaryRange; } public void setSalaryRange(String s) { this.salaryRange = s; }
    public String getJobType() { return jobType; } public void setJobType(String s) { this.jobType = s; }
}
