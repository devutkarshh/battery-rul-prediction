package com.jobportal.dto;

public class MatchRequest {
    private String resume_text;
    private String job_description;
    private String required_skills;

    public MatchRequest() {}
    public MatchRequest(String resumeText, String jobDescription, String requiredSkills) {
        this.resume_text = resumeText; this.job_description = jobDescription; this.required_skills = requiredSkills;
    }

    public String getResume_text() { return resume_text; } public void setResume_text(String s) { this.resume_text = s; }
    public String getJob_description() { return job_description; } public void setJob_description(String s) { this.job_description = s; }
    public String getRequired_skills() { return required_skills; } public void setRequired_skills(String s) { this.required_skills = s; }
}
