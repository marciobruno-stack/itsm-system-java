package com.itsm.incidentmanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ativo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Ativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    private String especificacoes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
