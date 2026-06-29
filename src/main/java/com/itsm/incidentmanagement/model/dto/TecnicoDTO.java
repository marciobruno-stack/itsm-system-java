package com.itsm.incidentmanagement.model.dto;

import java.util.Set;

public class TecnicoDTO {
    private Long id;
    private String nome;
    private String disponibilidade;
    private Integer cargaTrabalhoAtual;
    private Set<CompetenciaDTO> competencias;

    public TecnicoDTO() {}

    public TecnicoDTO(Long id, String nome, String disponibilidade, Integer cargaTrabalhoAtual, Set<CompetenciaDTO> competencias) {
        this.id = id;
        this.nome = nome;
        this.disponibilidade = disponibilidade;
        this.cargaTrabalhoAtual = cargaTrabalhoAtual;
        this.competencias = competencias;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDisponibilidade() { return disponibilidade; }
    public void setDisponibilidade(String disponibilidade) { this.disponibilidade = disponibilidade; }
    public Integer getCargaTrabalhoAtual() { return cargaTrabalhoAtual; }
    public void setCargaTrabalhoAtual(Integer cargaTrabalhoAtual) { this.cargaTrabalhoAtual = cargaTrabalhoAtual; }
    public Set<CompetenciaDTO> getCompetencias() { return competencias; }
    public void setCompetencias(Set<CompetenciaDTO> competencias) { this.competencias = competencias; }
}