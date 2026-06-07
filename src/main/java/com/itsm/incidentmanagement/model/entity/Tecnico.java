package com.itsm.incidentmanagement.model.entity;
import com.itsm.incidentmanagement.model.entity.Competencia;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;


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
    private String disponibilidade; // Guardar como JSON string, ou converter com AttributeConverter

    @Column(name = "carga_trabalho_atual")
    private Integer cargaTrabalhoAtual = 0;

    @ManyToMany
    @JoinTable(
            name = "tecnico_competencia",
            joinColumns = @JoinColumn(name = "tecnico_id"),
            inverseJoinColumns = @JoinColumn(name = "competencia_id")
    )
    private Set<Competencia> competencias = new HashSet<>();

    @OneToMany(mappedBy = "tecnico")
    @JsonIgnore
    private Set<Ticket> tickets = new HashSet<>();
}