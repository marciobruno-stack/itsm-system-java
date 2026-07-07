package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.security.service.JwtService;
import com.itsm.incidentmanagement.service.UtilizadorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuthController.class);

    private final UtilizadorService utilizadorService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ApiAuthController(UtilizadorService utilizadorService,
                             JwtService jwtService,
                             PasswordEncoder passwordEncoder) {
        this.utilizadorService = utilizadorService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        logger.info("ApiAuthController inicializado");
    }

    @PostMapping("/login")
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
        if (utilizador == null) {
            logger.debug("Tentativa de login com utilizador inexistente: {}", username);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro de autenticação");
            error.put("message", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        String hashBanco = utilizador.getPasswordHash();

        boolean matches = passwordEncoder.matches(password, hashBanco.replace("\\", ""));

        if (!matches) {
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

        logger.info("Login bem-sucedido para utilizador: {}", username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        try {
            String username = userData.get("username");
            String password = userData.get("password");
            String nome = userData.get("nome");
            String email = userData.get("email");
            String role = "UTILIZADOR"; // Role is always UTILIZADOR for self-registration

            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username é obrigatório"));
            }
            if (password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password é obrigatória"));
            }

            if (utilizadorService.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username já existe"));
            }

            Utilizador utilizador = new Utilizador();
            utilizador.setUsername(username);
            utilizador.setPasswordHash(passwordEncoder.encode(password));
            utilizador.setNome(nome != null ? nome : username);
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