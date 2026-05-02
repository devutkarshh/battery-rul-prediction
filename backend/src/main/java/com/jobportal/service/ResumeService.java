package com.jobportal.service;

import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final AIServiceClient aiServiceClient;
    private final String uploadDir;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
                         AIServiceClient aiServiceClient, @Value("${app.upload.dir}") String uploadDir) {
        this.resumeRepository = resumeRepository; this.userRepository = userRepository;
        this.aiServiceClient = aiServiceClient; this.uploadDir = uploadDir;
    }

    public Resume uploadResume(MultipartFile file, String userEmail) throws IOException {
        if (file.isEmpty()) throw new BadRequestException("File is empty");
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) throw new BadRequestException("File name is missing");
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        if (!fileExtension.equals("pdf") && !fileExtension.equals("docx"))
            throw new BadRequestException("Only PDF and DOCX files are supported");

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        String storedFileName = UUID.randomUUID() + "." + fileExtension;
        Path targetPath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String extractedText = "";
        try { extractedText = aiServiceClient.extractText(file.getBytes(), fileExtension, originalFileName); }
        catch (Exception e) { System.err.println("Warning: Text extraction failed: " + e.getMessage()); }

        Resume resume = new Resume();
        resume.setUser(user); resume.setFileName(originalFileName); resume.setFilePath(targetPath.toString());
        resume.setFileType(fileExtension); resume.setExtractedText(extractedText);
        return resumeRepository.save(resume);
    }

    public Resume getLatestResume(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        return resumeRepository.findTopByUserIdOrderByUploadedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No resume found. Please upload your resume first."));
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
    }
}
