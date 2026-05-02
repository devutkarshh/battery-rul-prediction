package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_id"}))
public class Application {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "job_id", nullable = false) private Job job;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "resume_id", nullable = false) private Resume resume;
    @Column(name = "match_score") private Double matchScore;
    @Column(name = "matched_skills", columnDefinition = "TEXT") private String matchedSkills;
    @Column(name = "missing_skills", columnDefinition = "TEXT") private String missingSkills;
    @Column(columnDefinition = "TEXT") private String recommendations;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Status status = Status.PENDING;
    @Column(name = "applied_at", updatable = false) private LocalDateTime appliedAt;

    @PrePersist protected void onCreate() { this.appliedAt = LocalDateTime.now(); }

    public enum Status { PENDING, REVIEWED, ACCEPTED, REJECTED }

    public Application() {}

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User u) { this.user = u; }
    public Job getJob() { return job; } public void setJob(Job j) { this.job = j; }
    public Resume getResume() { return resume; } public void setResume(Resume r) { this.resume = r; }
    public Double getMatchScore() { return matchScore; } public void setMatchScore(Double s) { this.matchScore = s; }
    public String getMatchedSkills() { return matchedSkills; } public void setMatchedSkills(String s) { this.matchedSkills = s; }
    public String getMissingSkills() { return missingSkills; } public void setMissingSkills(String s) { this.missingSkills = s; }
    public String getRecommendations() { return recommendations; } public void setRecommendations(String s) { this.recommendations = s; }
    public Status getStatus() { return status; } public void setStatus(Status s) { this.status = s; }
    public LocalDateTime getAppliedAt() { return appliedAt; } public void setAppliedAt(LocalDateTime t) { this.appliedAt = t; }
}
