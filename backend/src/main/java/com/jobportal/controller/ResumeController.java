package com.jobportal.controller;

import com.jobportal.entity.Resume;
import com.jobportal.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for resume upload and retrieval.
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * POST /api/resumes/upload — Upload a resume file (PDF/DOCX).
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        Resume resume = resumeService.uploadResume(file, authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("id", resume.getId());
        response.put("fileName", resume.getFileName());
        response.put("fileType", resume.getFileType());
        response.put("uploadedAt", resume.getUploadedAt());
        response.put("hasExtractedText", resume.getExtractedText() != null &&
                !resume.getExtractedText().isBlank());
        response.put("message", "Resume uploaded successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/resumes/my-resume — Get the current user's latest resume.
     */
    @GetMapping("/my-resume")
    public ResponseEntity<Map<String, Object>> getMyResume(Authentication authentication) {
        Resume resume = resumeService.getLatestResume(authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("id", resume.getId());
        response.put("fileName", resume.getFileName());
        response.put("fileType", resume.getFileType());
        response.put("uploadedAt", resume.getUploadedAt());
        response.put("hasExtractedText", resume.getExtractedText() != null &&
                !resume.getExtractedText().isBlank());

        return ResponseEntity.ok(response);
    }
}
