package com.jobportal.config;

import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Initializes seed data on startup if the database is empty or seed users are missing.
 * Re-hashes passwords with the application's BCryptPasswordEncoder to ensure they work.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Fix admin user password
            userRepository.findByEmail("admin@jobportal.com").ifPresentOrElse(
                admin -> {
                    // Re-hash password to ensure compatibility
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    userRepository.save(admin);
                    System.out.println("✅ Admin password updated.");
                },
                () -> {
                    User admin = new User();
                    admin.setFullName("Admin User");
                    admin.setEmail("admin@jobportal.com");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRole(User.Role.ROLE_ADMIN);
                    userRepository.save(admin);
                    System.out.println("✅ Admin user created.");
                }
            );

            // Fix regular user password
            userRepository.findByEmail("john@example.com").ifPresentOrElse(
                user -> {
                    user.setPassword(passwordEncoder.encode("user123"));
                    userRepository.save(user);
                    System.out.println("✅ User password updated.");
                },
                () -> {
                    User user = new User();
                    user.setFullName("John Doe");
                    user.setEmail("john@example.com");
                    user.setPassword(passwordEncoder.encode("user123"));
                    user.setRole(User.Role.ROLE_USER);
                    userRepository.save(user);
                    System.out.println("✅ User created.");
                }
            );
        };
    }
}
