package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository; this.userRepository = userRepository; this.applicationRepository = applicationRepository;
    }

    public List<JobResponse> getAllJobs() {
        return jobRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public JobResponse getJobById(Long id) {
        return mapToResponse(jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job", "id", id)));
    }

    public List<JobResponse> searchJobs(String query) {
        return jobRepository.findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(query, query)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public JobResponse createJob(JobRequest request, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Job job = new Job();
        job.setTitle(request.getTitle()); job.setCompany(request.getCompany());
        job.setLocation(request.getLocation()); job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills()); job.setSalaryRange(request.getSalaryRange());
        job.setJobType(Job.JobType.valueOf(request.getJobType())); job.setPostedBy(admin);
        return mapToResponse(jobRepository.save(job));
    }

    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
        job.setTitle(request.getTitle()); job.setCompany(request.getCompany());
        job.setLocation(request.getLocation()); job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills()); job.setSalaryRange(request.getSalaryRange());
        job.setJobType(Job.JobType.valueOf(request.getJobType()));
        return mapToResponse(jobRepository.save(job));
    }

    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) throw new ResourceNotFoundException("Job", "id", id);
        jobRepository.deleteById(id);
    }

    private JobResponse mapToResponse(Job job) {
        int count = applicationRepository.findByJobId(job.getId()).size();
        return new JobResponse(job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
                job.getDescription(), job.getRequiredSkills(), job.getSalaryRange(), job.getJobType().name(),
                job.getPostedBy().getFullName(), job.getCreatedAt(), job.getUpdatedAt(), count);
    }
}
