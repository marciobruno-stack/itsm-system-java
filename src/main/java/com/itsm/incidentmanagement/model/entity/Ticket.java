package com.itsm.incidentmanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 20)
    private String prioridade; // BAIXA, MEDIA, ALTA, CRITICA

    @Column(nullable = false, length = 20)
    private String tipo; // INCIDENTE, PEDIDO

    @Column(nullable = false, length = 30)
    private String estado; // ABERTO, ATRIBUIDO, EM_CURSO, RESOLVIDO, FECHADO

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_fecho")
    private LocalDateTime dataFecho;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    @ManyToOne
    @JoinColumn(name = "aberto_por_id", nullable = false)
    private Utilizador abertoPor;

    @ManyToOne
    @JoinColumn(name = "ativo_id")
    private Ativo ativo;
}