package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.repository.TicketRepository;
import com.itsm.incidentmanagement.utils.DisponibilidadeUtils;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketAssignmentService {
    private final CacheService cacheService;
    private final TicketRepository ticketRepository;

    public TicketAssignmentService(CacheService cacheService, TicketRepository ticketRepository) {
        this.cacheService = cacheService;
        this.ticketRepository = ticketRepository;
    }

    public Tecnico assignTechnician(Ticket ticket) {
        List<Tecnico> tecnicos = cacheService.getAllTecnicos();

        Set<String> palavrasChave = extrairPalavrasChave(ticket.getTitulo() + " " + ticket.getDescricao());
        List<Tecnico> qualificados = tecnicos.stream()
                .filter(t -> temCompetenciaRelevante(t, palavrasChave))
                .collect(Collectors.toList());

        // CORREÇÃO: usar LocalDate para obter o dia da semana
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        LocalTime agora = LocalTime.now();
        List<Tecnico> disponiveis = qualificados.stream()
                .filter(t -> DisponibilidadeUtils.isDisponivel(t.getDisponibilidade(), hoje, agora))
                .collect(Collectors.toList());

        PriorityQueue<Tecnico> heap = new PriorityQueue<>(Comparator.comparingInt(Tecnico::getCargaTrabalhoAtual));
        heap.addAll(disponiveis);

        Tecnico escolhido = heap.poll();
        if (escolhido != null) {
            escolhido.setCargaTrabalhoAtual(escolhido.getCargaTrabalhoAtual() + 1);
            cacheService.updateTecnico(escolhido);
            ticketRepository.save(ticket);
        }
        return escolhido;
    }

    private Set<String> extrairPalavrasChave(String texto) {
        Set<String> palavras = new HashSet<>();
        for (String p : texto.toLowerCase().split("\\s+")) {
            if (p.length() > 2) palavras.add(p);
        }
        return palavras;
    }

    private boolean temCompetenciaRelevante(Tecnico tecnico, Set<String> palavrasChave) {
        return tecnico.getCompetencias().stream()
                .anyMatch(comp -> palavrasChave.stream().anyMatch(p -> p.contains(comp.getNome().toLowerCase())));
    }
}