package com.jobportal.service;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.MatchResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeService resumeService;
    private final AIServiceClient aiServiceClient;

    public ApplicationService(ApplicationRepository applicationRepository, JobRepository jobRepository,
                              UserRepository userRepository, ResumeService resumeService, AIServiceClient aiServiceClient) {
        this.applicationRepository = applicationRepository; this.jobRepository = jobRepository;
        this.userRepository = userRepository; this.resumeService = resumeService; this.aiServiceClient = aiServiceClient;
    }

    public ApplicationResponse applyToJob(Long jobId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        if (applicationRepository.existsByUserIdAndJobId(user.getId(), jobId))
            throw new BadRequestException("You have already applied to this job");

        Resume resume = resumeService.getLatestResume(userEmail);
        if (resume.getExtractedText() == null || resume.getExtractedText().isBlank())
            throw new BadRequestException("Resume text could not be extracted. Please re-upload your resume.");

        MatchResponse matchResult;
        try {
            matchResult = aiServiceClient.matchResumeToJob(resume.getExtractedText(), job.getDescription(), job.getRequiredSkills());
        } catch (Exception e) {
            matchResult = new MatchResponse();
            matchResult.setMatchScore(0.0);
            matchResult.setMatchedSkills(new ArrayList<>());
            matchResult.setMissingSkills(new ArrayList<>());
            matchResult.setRecommendations(List.of("AI matching service is currently unavailable. Score will be updated later."));
        }

        Application app = new Application();
        app.setUser(user); app.setJob(job); app.setResume(resume);
        app.setMatchScore(matchResult.getMatchScore());
        app.setMatchedSkills(String.join(", ", matchResult.getMatchedSkills()));
        app.setMissingSkills(String.join(", ", matchResult.getMissingSkills()));
        app.setRecommendations(String.join(" | ", matchResult.getRecommendations()));
        app.setStatus(Application.Status.PENDING);
        return mapToResponse(applicationRepository.save(app));
    }

    public List<ApplicationResponse> getUserApplications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        return applicationRepository.findByUserId(user.getId()).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getJobApplicants(Long jobId) {
        if (!jobRepository.existsById(jobId)) throw new ResourceNotFoundException("Job", "id", jobId);
        return applicationRepository.findByJobIdOrderByMatchScoreDesc(jobId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ApplicationResponse getMatchResult(Long applicationId) {
        return mapToResponse(applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId)));
    }

    public ApplicationResponse updateStatus(Long applicationId, String status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        try { app.setStatus(Application.Status.valueOf(status.toUpperCase())); }
        catch (IllegalArgumentException e) { throw new BadRequestException("Invalid status: " + status); }
        return mapToResponse(applicationRepository.save(app));
    }

    private ApplicationResponse mapToResponse(Application app) {
        return new ApplicationResponse(app.getId(), app.getJob().getId(), app.getJob().getTitle(),
                app.getJob().getCompany(), app.getUser().getId(), app.getUser().getFullName(),
                app.getUser().getEmail(), app.getResume().getFileName(), app.getMatchScore(),
                app.getMatchedSkills(), app.getMissingSkills(), app.getRecommendations(),
                app.getStatus().name(), app.getAppliedAt());
    }
}
