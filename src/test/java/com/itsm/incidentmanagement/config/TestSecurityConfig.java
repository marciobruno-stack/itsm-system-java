package com.itsm.incidentmanagement.config;

import com.itsm.incidentmanagement.security.service.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtService testJwtService() {
        return new JwtService() {
            @Override
            public String generateToken(String username) {
                return "test-token";
            }
            @Override
            public String extractUsername(String token) {
                return "admin";
            }
            @Override
            public Boolean validateToken(String token, String username) {
                return true;
            }
        };
    }

    @Bean
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}