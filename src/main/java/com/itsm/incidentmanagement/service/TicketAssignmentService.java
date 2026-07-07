package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.repository.TicketRepository;
import com.itsm.incidentmanagement.utils.DisponibilidadeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketAssignmentService {
    private static final Logger logger = LoggerFactory.getLogger(TicketAssignmentService.class);

    private final CacheService cacheService;
    private final TicketRepository ticketRepository;

    public TicketAssignmentService(CacheService cacheService, TicketRepository ticketRepository) {
        this.cacheService = cacheService;
        this.ticketRepository = ticketRepository;
    }

    public Tecnico assignTechnician(Ticket ticket) {
        logger.info("========================================");
        logger.info("A atribuir técnico para ticket: {}", ticket.getTitulo());
        logger.info("========================================");

        // 1. Obter todos os técnicos da cache
        List<Tecnico> tecnicos = cacheService.getAllTecnicos();
        logger.debug("Total de técnicos na cache: {}", tecnicos.size());

        if (tecnicos.isEmpty()) {
            logger.debug("Nenhum técnico encontrado na cache! A recarregar...");
            cacheService.loadTecnicos();
            tecnicos = cacheService.getAllTecnicos();
            logger.debug("Após recarregar: {} técnicos", tecnicos.size());
        }

        if (tecnicos.isEmpty()) {
            logger.debug("Continua sem técnicos! Verificar base de dados.");
            return null;
        }

        // 2. Extrair palavras-chave
        Set<String> palavrasChave = extrairPalavrasChave(ticket.getTitulo() + " " + ticket.getDescricao());
        logger.debug("Palavras-chave extraídas: {}", palavrasChave);

        // 3. Mostrar competências de cada técnico (debug)
        tecnicos.forEach(t -> {
            String comps = t.getCompetencias().stream()
                    .map(c -> c.getNome())
                    .collect(Collectors.joining(", "));
            logger.debug("   {} | Competências: {} | Carga: {}",
                    t.getUtilizador().getNome(),
                    comps.isEmpty() ? "nenhuma" : comps,
                    t.getCargaTrabalhoAtual());
        });

        // 4. Filtrar por competências (usando o método melhorado)
        List<Tecnico> qualificados = tecnicos.stream()
                .filter(t -> temCompetenciaRelevante(t, palavrasChave))
                .collect(Collectors.toList());
        logger.debug("Técnicos com competências relevantes: {}", qualificados.size());

        if (qualificados.isEmpty()) {
            logger.debug("Nenhum técnico com competências relevantes!");
            return null;
        }

        // 5. Filtrar por disponibilidade (agora ativo)
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        LocalTime agora = LocalTime.now();
        logger.debug("Dia atual: {} | Hora: {}", hoje, agora);

        List<Tecnico> disponiveis = qualificados.stream()
                .filter(t -> DisponibilidadeUtils.isDisponivel(t.getDisponibilidade(), hoje, agora))
                .collect(Collectors.toList());
        logger.debug("Técnicos disponíveis (horário): {}", disponiveis.size());

        if (disponiveis.isEmpty()) {
            logger.debug("Nenhum técnico disponível no horário atual!");
            return null;
        }

        // 6. Heap (PriorityQueue) por carga de trabalho (menor carga primeiro)
        PriorityQueue<Tecnico> heap = new PriorityQueue<>(
                Comparator.comparingInt(Tecnico::getCargaTrabalhoAtual)
        );
        heap.addAll(disponiveis);

        // 7. Escolher o técnico com menor carga
        Tecnico escolhido = heap.poll();
        if (escolhido != null) {
            logger.info("Técnico ESCOLHIDO: {} (carga atual: {})",
                    escolhido.getUtilizador().getNome(), escolhido.getCargaTrabalhoAtual());
            escolhido.setCargaTrabalhoAtual(escolhido.getCargaTrabalhoAtual() + 1);
            cacheService.updateTecnico(escolhido);
            ticketRepository.save(ticket);
        } else {
            logger.debug("Nenhum técnico disponível (heap vazio)!");
        }

        logger.info("========================================");
        return escolhido;
    }

    private Set<String> extrairPalavrasChave(String texto) {
        Set<String> palavras = new HashSet<>();
        if (texto == null || texto.isBlank()) {
            return palavras;
        }
        for (String p : texto.toLowerCase().split("\\s+")) {
            // Remove caracteres especiais
            p = p.replaceAll("[^a-zA-Z0-9]", "");
            if (p.length() > 2) {
                palavras.add(p);
            }
        }
        return palavras;
    }

    private boolean temCompetenciaRelevante(Tecnico tecnico, Set<String> palavrasChave) {
        if (palavrasChave.isEmpty()) {
            return true; // Se não há palavras-chave, aceita qualquer técnico
        }
        return tecnico.getCompetencias().stream()
                .anyMatch(comp -> {
                    String compNome = comp.getNome().toLowerCase();
                    // Verifica se alguma palavra-chave contém a competência OU vice-versa
                    return palavrasChave.stream().anyMatch(palavra ->
                            palavra.contains(compNome) || compNome.contains(palavra)
                    );
                });
    }
}