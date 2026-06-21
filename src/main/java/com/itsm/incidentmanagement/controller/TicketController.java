package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<List<Ticket>> findAll() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ticket por ID")
    public ResponseEntity<Ticket> findById(@PathVariable Long id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar tickets por estado")
    public ResponseEntity<List<Ticket>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ticketService.findByEstado(estado));
    }

    @PostMapping
    @Operation(summary = "Criar novo ticket (com atribuição automática)")
    public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
        Ticket created = ticketService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ticket")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticket) {
        Ticket updated = ticketService.update(id, ticket);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Alterar estado do ticket")
    public ResponseEntity<Ticket> updateEstado(@PathVariable Long id, @RequestParam String estado) {
        Ticket updated = ticketService.updateEstado(id, estado);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover ticket")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}