package com.itsm.incidentmanagement.security.config;

import com.itsm.incidentmanagement.security.filter.JwtAuthenticationFilter;
import com.itsm.incidentmanagement.security.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        logger.info("SecurityConfig inicializado!");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("PasswordEncoder BCrypt criado");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Modo seguranÃ§a ativado (JWT + Web)");

        http
                // CORS - ConfiguraÃ§Ã£o para APIs
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF - DESABILITADO PARA /login E /api/**
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/login", "/api/**", "/logout")
                )
                .authenticationManager(authenticationManager())
                .authorizeHttpRequests(authz -> authz
                        // Endpoints PÃšBLICOS (sem autenticaÃ§Ã£o)
                        .requestMatchers("/", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // API - AutenticaÃ§Ã£o (pÃºblico)
                        .requestMatchers("/api/auth/**").permitAll()

                        // API - Protegidos por JWT
                        .requestMatchers("/api/tickets/**").hasAnyRole("ADMIN", "TECNICO", "UTILIZADOR")
                        .requestMatchers("/api/tecnicos/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/ativos/**").hasRole("ADMIN")
                        .requestMatchers("/api/competencias/**").hasRole("ADMIN")
                        .requestMatchers("/api/utilizadores/**").hasRole("ADMIN")

                        // Web - Protegidos por SessÃ£o
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/tecnico/**").hasRole("TECNICO")
                        .requestMatchers("/utilizador/**").hasAnyRole("UTILIZADOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                // Login Web (formulÃ¡rio) com redirecionamento por role
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(this::handleLoginSuccess)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // Logout
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // API Stateless (JWT) - NÃ£o guardar sessÃ£o para APIs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // Frame Options - Permitir H2 Console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // MÃ©todo para redirecionar por role
    private void handleLoginSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    org.springframework.security.core.Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            logger.info("Redirecionando por role: {}", role);

            if ("ROLE_ADMIN".equals(role)) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if ("ROLE_TECNICO".equals(role)) {
                response.sendRedirect("/tecnico/dashboard");
                return;
            } else if ("ROLE_UTILIZADOR".equals(role)) {
                response.sendRedirect("/utilizador/dashboard");
                return;
            }
        }

        // Fallback
        response.sendRedirect("/");
    }
}