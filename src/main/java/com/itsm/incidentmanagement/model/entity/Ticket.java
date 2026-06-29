package com.itsm.incidentmanagement.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 20)
    private String prioridade;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, length = 30)
    private String estado;

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

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDateTime getDataFecho() { return dataFecho; }
    public void setDataFecho(LocalDateTime dataFecho) { this.dataFecho = dataFecho; }
    public Tecnico getTecnico() { return tecnico; }
    public void setTecnico(Tecnico tecnico) { this.tecnico = tecnico; }
    public Utilizador getAbertoPor() { return abertoPor; }
    public void setAbertoPor(Utilizador abertoPor) { this.abertoPor = abertoPor; }
    public Ativo getAtivo() { return ativo; }
    public void setAtivo(Ativo ativo) { this.ativo = ativo; }
}