package com.jobportal.controller;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for job application operations.
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * POST /api/applications/apply — Apply to a job (triggers AI matching).
     */
    @PostMapping("/apply")
    public ResponseEntity<ApplicationResponse> applyToJob(
            @RequestBody Map<String, Long> request,
            Authentication authentication) {
        Long jobId = request.get("jobId");
        if (jobId == null) {
            return ResponseEntity.badRequest().build();
        }

        ApplicationResponse response = applicationService.applyToJob(
                jobId, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/applications/my-applications — Get current user's applications.
     */
    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getUserApplications(authentication.getName()));
    }

    /**
     * GET /api/applications/job/{jobId} — Get applicants for a job, ranked by score (admin).
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getJobApplicants(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getJobApplicants(jobId));
    }

    /**
     * GET /api/applications/{id}/match-result — Get match result for an application.
     */
    @GetMapping("/{id}/match-result")
    public ResponseEntity<ApplicationResponse> getMatchResult(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getMatchResult(id));
    }

    /**
     * PUT /api/applications/{id}/status — Update application status (admin).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }
}
