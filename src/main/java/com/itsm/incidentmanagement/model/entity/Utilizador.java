package com.itsm.incidentmanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;  // ← ADICIONAR
import java.time.LocalDateTime;

@Entity
@Table(name = "utilizador")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "utilizador")
    @JsonIgnore   // ← ADICIONAR: evita loop com Tecnico
    private Tecnico tecnico;
}