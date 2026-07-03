package com.itsm.incidentmanagement.security.config;

import com.itsm.incidentmanagement.security.filter.JwtAuthenticationFilter;
import com.itsm.incidentmanagement.security.service.CustomUserDetailsService;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        System.out.println("✅ SecurityConfig inicializado!");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("[LOGIN] PasswordEncoder BCrypt CRIADO!");
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== 🔒 MODO SEGURANÇA ATIVADO (JWT + Web) ===");

        http
                // ⭐ CSRF - DESABILITADO PARA /login E /api/**
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/login", "/api/**", "/logout")
                )
                .authenticationManager(authenticationManager())
                .authorizeHttpRequests(authz -> authz
                        // 🔓 Endpoints PÚBLICOS (sem autenticação)
                        .requestMatchers("/", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // 🔓 API - Autenticação (público)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🔒 API - Protegidos por JWT
                        .requestMatchers("/api/tickets/**").hasAnyRole("ADMIN", "TECNICO", "UTILIZADOR")
                        .requestMatchers("/api/tecnicos/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/ativos/**").hasRole("ADMIN")
                        .requestMatchers("/api/competencias/**").hasRole("ADMIN")
                        .requestMatchers("/api/utilizadores/**").hasRole("ADMIN")

                        // 🔒 Web - Protegidos por Sessão
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/tecnico/**").hasRole("TECNICO")
                        .requestMatchers("/utilizador/**").hasAnyRole("UTILIZADOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                // Login Web (formulário) com redirecionamento por role
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
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // ⭐ API Stateless (JWT) - Não guardar sessão para APIs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // ⭐ Frame Options - Permitir H2 Console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ⭐ Método para redirecionar por role
    private void handleLoginSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    org.springframework.security.core.Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            System.out.println("🔑 Redirecionando por role: " + role);

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