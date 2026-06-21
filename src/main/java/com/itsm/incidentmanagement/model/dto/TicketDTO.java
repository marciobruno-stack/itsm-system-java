package com.itsm.incidentmanagement.model.dto;

import lombok.Data;

@Data
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
}