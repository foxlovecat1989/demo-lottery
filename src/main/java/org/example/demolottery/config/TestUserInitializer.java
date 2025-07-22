package org.example.demolottery.config;

import org.example.demolottery.entity.User;
import org.example.demolottery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;

@Configuration
public class TestUserInitializer {
    @Bean
    public CommandLineRunner createTestUser(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("testuser").isEmpty()) {
                User user = new User();
                user.setUsername("testuser");
                user.setPassword(new BCryptPasswordEncoder().encode("testpass"));
                user.setEmail("testuser@example.com");
                user.setEnabled(true);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                user.setRoles(new HashSet<>());
                user.getRoles().add(User.Role.USER);
                userRepository.save(user);
                System.out.println("[TestUserInitializer] testuser/testpass created");
            }
        };
    }
} 