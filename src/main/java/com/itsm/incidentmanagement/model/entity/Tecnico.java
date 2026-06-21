package com.itsm.incidentmanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tecnico")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilizador_id", unique = true, nullable = false)
    private Utilizador utilizador;

    @Column(length = 2000)
    private String disponibilidade;

    @Column(name = "carga_trabalho_atual")
    private Integer cargaTrabalhoAtual = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tecnico_competencia",
            joinColumns = @JoinColumn(name = "tecnico_id"),
            inverseJoinColumns = @JoinColumn(name = "competencia_id")
    )
    private Set<Competencia> competencias = new HashSet<>();

    @OneToMany(mappedBy = "tecnico")
    @JsonIgnore  // ← JÁ TEM (evita loop com Ticket)
    private Set<Ticket> tickets = new HashSet<>();
}