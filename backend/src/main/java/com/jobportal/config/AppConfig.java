package com.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application-wide bean configuration.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate bean for making HTTP calls to the Python AI service.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
