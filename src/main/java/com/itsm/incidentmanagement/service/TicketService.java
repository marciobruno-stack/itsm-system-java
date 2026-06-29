package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final TecnicoRepository tecnicoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final TicketAssignmentService ticketAssignmentService;
    private final AuditService auditService;

    public TicketService(TicketRepository ticketRepository,
                         TecnicoRepository tecnicoRepository,
                         UtilizadorRepository utilizadorRepository,
                         TicketAssignmentService ticketAssignmentService,
                         AuditService auditService) {
        this.ticketRepository = ticketRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.ticketAssignmentService = ticketAssignmentService;
        this.auditService = auditService;
    }

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        logger.info("AUDIT - Criação de ticket: {}", ticket.getTitulo());
        auditService.logTicketCreation(0L, ticket.getTitulo(),
                ticket.getAbertoPor() != null ? ticket.getAbertoPor().getId() : null, "INICIADO");

        ticket.setDataAbertura(LocalDateTime.now());
        ticket.setEstado("ABERTO");

        try {
            Tecnico tecnico = ticketAssignmentService.assignTechnician(ticket);
            if (tecnico != null) {
                ticket.setTecnico(tecnico);
                tecnico.setCargaTrabalhoAtual(tecnico.getCargaTrabalhoAtual() + 1);
                tecnicoRepository.save(tecnico);
                logger.info("✅ AUDIT - Ticket {} atribuído ao técnico {} (carga: {})",
                        ticket.getId(), tecnico.getId(), tecnico.getCargaTrabalhoAtual());
                auditService.logTicketAssignment(
                        ticket.getId() != null ? ticket.getId() : 0L,
                        tecnico.getId(),
                        ticket.getAbertoPor() != null ? ticket.getAbertoPor().getId() : null
                );
            } else {
                logger.warn("⚠️ AUDIT - Ticket {} sem técnico disponível", ticket.getId());
            }
        } catch (Exception e) {
            logger.error("❌ Erro no algoritmo de atribuição: {}", e.getMessage());
            auditService.logError("ATRIBUICAO_TICKET", e.getMessage(), ticket.getTitulo());
        }

        Ticket saved = ticketRepository.save(ticket);
        logger.info("AUDIT - Ticket criado com ID: {}", saved.getId());
        auditService.logTicketCreation(saved.getId(), saved.getTitulo(),
                saved.getAbertoPor() != null ? saved.getAbertoPor().getId() : null,
                saved.getEstado());

        return saved;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findByEstado(String estado) {
        return ticketRepository.findByEstado(estado);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Transactional
    public Ticket updateEstado(Long id, String estado) {
        logger.info("AUDIT - Atualizando estado do ticket {} para {}", id, estado);

        Ticket ticket = findById(id);
        if (ticket != null) {
            String oldState = ticket.getEstado();

            if ("RESOLVIDO".equals(estado) || "FECHADO".equals(estado)) {
                Tecnico tecnico = ticket.getTecnico();
                if (tecnico != null && tecnico.getCargaTrabalhoAtual() > 0) {
                    tecnico.setCargaTrabalhoAtual(tecnico.getCargaTrabalhoAtual() - 1);
                    tecnicoRepository.save(tecnico);
                    logger.info("AUDIT - Carga do técnico {} reduzida para {}",
                            tecnico.getId(), tecnico.getCargaTrabalhoAtual());
                }
            }

            ticket.setEstado(estado);
            if ("RESOLVIDO".equals(estado) || "FECHADO".equals(estado)) {
                ticket.setDataFecho(LocalDateTime.now());
                logger.info("AUDIT - Ticket {} resolvido em {}", id, ticket.getDataFecho());
            }

            auditService.logTicketStateChange(id, oldState, estado,
                    ticket.getAbertoPor() != null ? ticket.getAbertoPor().getId() : null);

            return ticketRepository.save(ticket);
        }
        logger.error("Ticket {} não encontrado para atualização", id);
        return null;
    }

    @Transactional
    public Ticket update(Long id, Ticket ticketDetails) {
        logger.info("AUDIT - Atualizando ticket {}", id);
        Ticket ticket = findById(id);
        if (ticket != null) {
            if (ticketDetails.getTitulo() != null) ticket.setTitulo(ticketDetails.getTitulo());
            if (ticketDetails.getDescricao() != null) ticket.setDescricao(ticketDetails.getDescricao());
            if (ticketDetails.getPrioridade() != null) ticket.setPrioridade(ticketDetails.getPrioridade());
            if (ticketDetails.getEstado() != null) ticket.setEstado(ticketDetails.getEstado());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("AUDIT - Removendo ticket {}", id);
        Ticket ticket = findById(id);
        if (ticket != null && ticket.getTecnico() != null) {
            Tecnico tecnico = ticket.getTecnico();
            if (tecnico.getCargaTrabalhoAtual() > 0) {
                tecnico.setCargaTrabalhoAtual(tecnico.getCargaTrabalhoAtual() - 1);
                tecnicoRepository.save(tecnico);
            }
        }
        ticketRepository.deleteById(id);
    }

    public List<Ticket> findByTecnicoId(Long tecnicoId) {
        return ticketRepository.findByTecnicoId(tecnicoId);
    }

    public List<Ticket> findByAbertoPorId(Long utilizadorId) {
        return ticketRepository.findByAbertoPorId(utilizadorId);
    }
}