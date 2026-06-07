package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketAssignmentService assignmentService;

    public TicketService(TicketRepository ticketRepository, TicketAssignmentService assignmentService) {
        this.ticketRepository = ticketRepository;
        this.assignmentService = assignmentService;
    }

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        ticket.setDataAbertura(LocalDateTime.now());
        ticket.setEstado("ABERTO");
        Ticket saved = ticketRepository.save(ticket);
        // Tenta atribuir automaticamente
        var tecnico = assignmentService.assignTechnician(saved);
        if (tecnico != null) {
            saved.setTecnico(tecnico);
            saved.setEstado("ATRIBUIDO");
            ticketRepository.save(saved);
        }
        return saved;
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    // outros métodos: update, delete, list
}