package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketAssignmentService assignmentService;

    public TicketService(TicketRepository ticketRepository, TicketAssignmentService assignmentService) {
        this.ticketRepository = ticketRepository;
        this.assignmentService = assignmentService;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public List<Ticket> findByEstado(String estado) {
        return ticketRepository.findByEstado(estado);
    }

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        System.out.println("📝 Criando ticket: " + ticket.getTitulo());

        // Validações
        if (ticket.getTitulo() == null || ticket.getTitulo().isBlank()) {
            throw new RuntimeException("Título é obrigatório");
        }
        if (ticket.getDescricao() == null || ticket.getDescricao().isBlank()) {
            throw new RuntimeException("Descrição é obrigatória");
        }
        if (ticket.getAbertoPor() == null || ticket.getAbertoPor().getId() == null) {
            throw new RuntimeException("Utilizador que abre o ticket é obrigatório");
        }

        ticket.setDataAbertura(LocalDateTime.now());
        ticket.setEstado("ABERTO");
        Ticket saved = ticketRepository.save(ticket);
        System.out.println("💾 Ticket salvo com ID: " + saved.getId());

        System.out.println("🔄 A tentar atribuir técnico...");
        try {
            Tecnico tecnico = assignmentService.assignTechnician(saved);
            if (tecnico != null) {
                System.out.println("✅ Técnico atribuído: " + tecnico.getUtilizador().getNome());
                saved.setTecnico(tecnico);
                saved.setEstado("ATRIBUIDO");
                ticketRepository.save(saved);
            } else {
                System.out.println("❌ Nenhum técnico disponível para atribuição!");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro na atribuição: " + e.getMessage());
            e.printStackTrace();
        }
        return saved;
    }

    @Transactional
    public Ticket update(Long id, Ticket ticket) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        if (ticket.getTitulo() != null && !ticket.getTitulo().isBlank()) {
            existing.setTitulo(ticket.getTitulo());
        }
        if (ticket.getDescricao() != null && !ticket.getDescricao().isBlank()) {
            existing.setDescricao(ticket.getDescricao());
        }
        if (ticket.getPrioridade() != null && !ticket.getPrioridade().isBlank()) {
            existing.setPrioridade(ticket.getPrioridade());
        }
        if (ticket.getTipo() != null && !ticket.getTipo().isBlank()) {
            existing.setTipo(ticket.getTipo());
        }
        if (ticket.getAtivo() != null && ticket.getAtivo().getId() != null) {
            existing.setAtivo(ticket.getAtivo());
        }

        return ticketRepository.save(existing);
    }

    @Transactional
    public Ticket updateEstado(Long id, String estado) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        String estadoUpper = estado.toUpperCase();
        if (!estadoUpper.matches("ABERTO|ATRIBUIDO|EM_CURSO|RESOLVIDO|FECHADO")) {
            throw new RuntimeException("Estado inválido. Use: ABERTO, ATRIBUIDO, EM_CURSO, RESOLVIDO ou FECHADO");
        }

        existing.setEstado(estadoUpper);

        if ("FECHADO".equals(estadoUpper)) {
            existing.setDataFecho(LocalDateTime.now());
            if (existing.getTecnico() != null) {
                Tecnico tecnico = existing.getTecnico();
                int cargaAtual = tecnico.getCargaTrabalhoAtual();
                if (cargaAtual > 0) {
                    tecnico.setCargaTrabalhoAtual(cargaAtual - 1);
                }
            }
        }

        return ticketRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        if (ticket.getTecnico() != null && !"FECHADO".equals(ticket.getEstado())) {
            Tecnico tecnico = ticket.getTecnico();
            int cargaAtual = tecnico.getCargaTrabalhoAtual();
            if (cargaAtual > 0) {
                tecnico.setCargaTrabalhoAtual(cargaAtual - 1);
            }
        }

        ticketRepository.deleteById(id);
    }
}