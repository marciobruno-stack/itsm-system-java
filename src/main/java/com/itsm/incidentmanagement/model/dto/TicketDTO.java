package com.itsm.incidentmanagement.model.dto;

public class TicketDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String prioridade;
    private String tipo;
    private String estado;
    private String dataAbertura;
    private String dataFecho;
    private Long tecnicoId;
    private String tecnicoNome;
    private Long abertoPorId;
    private String abertoPorNome;
    private Long ativoId;
    private String ativoNome;

    public TicketDTO() {}

    public TicketDTO(Long id, String titulo, String descricao, String prioridade,
                     String tipo, String estado, String dataAbertura, String dataFecho,
                     Long tecnicoId, String tecnicoNome, Long abertoPorId,
                     String abertoPorNome, Long ativoId, String ativoNome) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.tipo = tipo;
        this.estado = estado;
        this.dataAbertura = dataAbertura;
        this.dataFecho = dataFecho;
        this.tecnicoId = tecnicoId;
        this.tecnicoNome = tecnicoNome;
        this.abertoPorId = abertoPorId;
        this.abertoPorNome = abertoPorNome;
        this.ativoId = ativoId;
        this.ativoNome = ativoNome;
    }

    // ==================== GETTERS ====================
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getPrioridade() { return prioridade; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public String getDataAbertura() { return dataAbertura; }
    public String getDataFecho() { return dataFecho; }
    public Long getTecnicoId() { return tecnicoId; }
    public String getTecnicoNome() { return tecnicoNome; }
    public Long getAbertoPorId() { return abertoPorId; }
    public String getAbertoPorNome() { return abertoPorNome; }
    public Long getAtivoId() { return ativoId; }
    public String getAtivoNome() { return ativoNome; }

    // ==================== SETTERS ====================
    public void setId(Long id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }
    public void setDataFecho(String dataFecho) { this.dataFecho = dataFecho; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
    public void setTecnicoNome(String tecnicoNome) { this.tecnicoNome = tecnicoNome; }
    public void setAbertoPorId(Long abertoPorId) { this.abertoPorId = abertoPorId; }
    public void setAbertoPorNome(String abertoPorNome) { this.abertoPorNome = abertoPorNome; }
    public void setAtivoId(Long ativoId) { this.ativoId = ativoId; }
    public void setAtivoNome(String ativoNome) { this.ativoNome = ativoNome; }
}