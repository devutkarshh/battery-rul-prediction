package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "file_name", nullable = false) private String fileName;
    @Column(name = "file_path", nullable = false, length = 500) private String filePath;
    @Column(name = "file_type", nullable = false, length = 10) private String fileType;
    @Column(name = "extracted_text", columnDefinition = "LONGTEXT") private String extractedText;
    @Column(name = "uploaded_at", updatable = false) private LocalDateTime uploadedAt;

    @PrePersist protected void onCreate() { this.uploadedAt = LocalDateTime.now(); }

    public Resume() {}

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User u) { this.user = u; }
    public String getFileName() { return fileName; } public void setFileName(String s) { this.fileName = s; }
    public String getFilePath() { return filePath; } public void setFilePath(String s) { this.filePath = s; }
    public String getFileType() { return fileType; } public void setFileType(String s) { this.fileType = s; }
    public String getExtractedText() { return extractedText; } public void setExtractedText(String s) { this.extractedText = s; }
    public LocalDateTime getUploadedAt() { return uploadedAt; } public void setUploadedAt(LocalDateTime t) { this.uploadedAt = t; }
}
