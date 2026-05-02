package com.jobportal.service;

import com.jobportal.dto.MatchRequest;
import com.jobportal.dto.MatchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP client for communicating with the Python AI service.
 */
@Service
public class AIServiceClient {

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AIServiceClient(RestTemplate restTemplate,
                           @Value("${app.ai-service.url}") String aiServiceUrl) {
        this.restTemplate = restTemplate;
        this.aiServiceUrl = aiServiceUrl;
    }

    /**
     * Send resume text and job description to the AI service for matching.
     *
     * @param resumeText     Extracted resume text
     * @param jobDescription Job description text
     * @param requiredSkills Comma-separated required skills
     * @return MatchResponse with score and skill analysis
     */
    public MatchResponse matchResumeToJob(String resumeText, String jobDescription,
                                           String requiredSkills) {
        String url = aiServiceUrl + "/api/match";

        MatchRequest request = new MatchRequest(resumeText, jobDescription, requiredSkills);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MatchRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MatchResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, MatchResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new RuntimeException("AI service returned unexpected response: " +
                    response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with AI service: " +
                    e.getMessage(), e);
        }
    }

    /**
     * Send a file to the AI service for text extraction.
     *
     * @param fileBytes Raw file bytes
     * @param fileType  File extension (pdf/docx)
     * @param fileName  Original file name
     * @return Extracted text
     */
    public String extractText(byte[] fileBytes, String fileType, String fileName) {
        String url = aiServiceUrl + "/api/extract";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Build multipart body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Boolean success = (Boolean) responseBody.get("success");
                if (Boolean.TRUE.equals(success)) {
                    return (String) responseBody.get("extracted_text");
                }
            }

            return "";
        } catch (Exception e) {
            System.err.println("Text extraction via AI service failed: " + e.getMessage());
            return "";
        }
    }
}
