package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, length = 200) private String company;
    @Column(length = 150) private String location;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Column(name = "required_skills", nullable = false, columnDefinition = "TEXT") private String requiredSkills;
    @Column(name = "salary_range", length = 100) private String salaryRange;
    @Enumerated(EnumType.STRING) @Column(name = "job_type", nullable = false) private JobType jobType = JobType.FULL_TIME;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "posted_by", nullable = false) private User postedBy;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum JobType { FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT }

    public Job() {}

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getCompany() { return company; } public void setCompany(String company) { this.company = company; }
    public String getLocation() { return location; } public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getRequiredSkills() { return requiredSkills; } public void setRequiredSkills(String s) { this.requiredSkills = s; }
    public String getSalaryRange() { return salaryRange; } public void setSalaryRange(String s) { this.salaryRange = s; }
    public JobType getJobType() { return jobType; } public void setJobType(JobType t) { this.jobType = t; }
    public User getPostedBy() { return postedBy; } public void setPostedBy(User u) { this.postedBy = u; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
