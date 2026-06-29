package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.security.service.JwtService;
import com.itsm.incidentmanagement.service.UtilizadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registo")
public class ApiAuthController {

    private final UtilizadorService utilizadorService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ApiAuthController(UtilizadorService utilizadorService,
                             JwtService jwtService,
                             PasswordEncoder passwordEncoder) {
        this.utilizadorService = utilizadorService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar utilizador e obter token JWT")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro de validação");
            error.put("message", "Username e password são obrigatórios");
            return ResponseEntity.badRequest().body(error);
        }

        Utilizador utilizador = utilizadorService.findByUsername(username);
        if (utilizador == null || !passwordEncoder.matches(password, utilizador.getPasswordHash())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro de autenticação");
            error.put("message", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        String token = jwtService.generateToken(username);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("role", utilizador.getRole());
        response.put("nome", utilizador.getNome());

        System.out.println("✅ Login bem-sucedido: " + username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registar novo utilizador")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        try {
            String username = userData.get("username");
            String password = userData.get("password");
            String nome = userData.get("nome");
            String email = userData.get("email");
            String role = userData.getOrDefault("role", "UTILIZADOR");

            // Validações
            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username é obrigatório"));
            }
            if (password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password é obrigatória"));
            }
            if (nome == null || nome.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nome é obrigatório"));
            }

            // Verificar se já existe
            if (utilizadorService.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username já existe"));
            }
            if (email != null && !email.isBlank() && utilizadorService.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email já existe"));
            }

            // Criar utilizador
            Utilizador utilizador = new Utilizador();
            utilizador.setUsername(username);
            utilizador.setPasswordHash(passwordEncoder.encode(password));
            utilizador.setNome(nome);
            utilizador.setEmail(email != null ? email : username + "@itsm.com");
            utilizador.setRole(role);
            utilizador.setCreatedAt(LocalDateTime.now());

            Utilizador saved = utilizadorService.save(utilizador);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilizador criado com sucesso");
            response.put("userId", saved.getId());
            response.put("username", saved.getUsername());
            response.put("role", saved.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erro ao registar utilizador",
                    "message", e.getMessage()
            ));
        }
    }
}