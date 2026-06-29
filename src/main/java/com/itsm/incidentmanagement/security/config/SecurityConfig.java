package com.itsm.incidentmanagement.security.config;

import com.itsm.incidentmanagement.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        System.out.println("✅ SecurityConfig inicializado!");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("[LOGIN] PasswordEncoder BCrypt CRIADO!");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== 🔓 MODO TESTE - SEGURANÇA DESATIVADA ===");
        System.out.println("⚠️  TODAS AS REQUISIÇÕES PERMITIDAS!");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // ⚠️ TODAS AS REQUISIÇÕES PERMITIDAS!
                );

        return http.build();
    }
}