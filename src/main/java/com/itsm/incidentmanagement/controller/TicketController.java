package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.dto.TicketDTO;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoints para gestão de tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os tickets")
    public ResponseEntity<List<TicketDTO>> findAll() {
        List<Ticket> tickets = ticketService.findAll();
        List<TicketDTO> dtos = tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ticket por ID")
    public ResponseEntity<TicketDTO> findById(@PathVariable Long id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(ticket));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar tickets por estado")
    public ResponseEntity<List<TicketDTO>> findByEstado(@PathVariable String estado) {
        List<Ticket> tickets = ticketService.findByEstado(estado);
        List<TicketDTO> dtos = tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(summary = "Criar novo ticket")
    public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
        try {
            Ticket saved = ticketService.createTicket(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ticket")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticket) {
        Ticket existing = ticketService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Ticket updated = ticketService.update(id, ticket);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    // ⭐ CORRIGIDO - Método delete com tratamento de exceções
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover ticket")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Ticket existing = ticketService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            ticketService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Método para converter Ticket para TicketDTO
    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitulo(ticket.getTitulo());
        dto.setDescricao(ticket.getDescricao());
        dto.setPrioridade(ticket.getPrioridade());
        dto.setTipo(ticket.getTipo());
        dto.setEstado(ticket.getEstado());
        dto.setDataAbertura(ticket.getDataAbertura() != null ? ticket.getDataAbertura().toString() : null);
        dto.setDataFecho(ticket.getDataFecho() != null ? ticket.getDataFecho().toString() : null);

        if (ticket.getTecnico() != null) {
            dto.setTecnicoId(ticket.getTecnico().getId());
            dto.setTecnicoNome(ticket.getTecnico().getUtilizador().getNome());
        }

        if (ticket.getAbertoPor() != null) {
            dto.setAbertoPorId(ticket.getAbertoPor().getId());
            dto.setAbertoPorNome(ticket.getAbertoPor().getNome());
        }

        if (ticket.getAtivo() != null) {
            dto.setAtivoId(ticket.getAtivo().getId());
            dto.setAtivoNome(ticket.getAtivo().getNome());
        }

        return dto;
    }
}