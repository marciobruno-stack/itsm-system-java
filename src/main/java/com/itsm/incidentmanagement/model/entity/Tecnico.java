package com.itsm.incidentmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itsm.incidentmanagement.model.dto.CompetenciaDTO;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "tecnico")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonIgnoreProperties({"tecnicos"})
    private Set<Competencia> competencias = new HashSet<>();

    @OneToMany(mappedBy = "tecnico")
    @JsonIgnore
    private Set<Ticket> tickets = new HashSet<>();

    // ⭐ Método para serializar competências como DTO
    @JsonProperty("competencias")
    public Set<CompetenciaDTO> getCompetenciasDTO() {
        return competencias.stream()
                .map(c -> new CompetenciaDTO(c.getId(), c.getNome()))
                .collect(Collectors.toSet());
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilizador getUtilizador() { return utilizador; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
    public String getDisponibilidade() { return disponibilidade; }
    public void setDisponibilidade(String disponibilidade) { this.disponibilidade = disponibilidade; }
    public Integer getCargaTrabalhoAtual() { return cargaTrabalhoAtual; }
    public void setCargaTrabalhoAtual(Integer cargaTrabalhoAtual) { this.cargaTrabalhoAtual = cargaTrabalhoAtual; }
    public Set<Competencia> getCompetencias() { return competencias; }
    public void setCompetencias(Set<Competencia> competencias) { this.competencias = competencias; }
    public Set<Ticket> getTickets() { return tickets; }
    public void setTickets(Set<Ticket> tickets) { this.tickets = tickets; }
}