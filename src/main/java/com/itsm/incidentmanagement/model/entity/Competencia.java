package com.itsm.incidentmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "competencia")
public class Competencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nome;

    @ManyToMany(mappedBy = "competencias")
    @JsonIgnore  // ⭐ ADICIONAR PARA EVITAR LOOP INFINITO
    private Set<Tecnico> tecnicos = new HashSet<>();

    public Competencia() {}

    public Competencia(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Set<Tecnico> getTecnicos() { return tecnicos; }
    public void setTecnicos(Set<Tecnico> tecnicos) { this.tecnicos = tecnicos; }
}