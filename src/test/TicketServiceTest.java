package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private UtilizadorRepository utilizadorRepository;

    @Mock
    private TicketAssignmentService assignmentService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private Tecnico tecnico;
    private Utilizador utilizador;

    @BeforeEach
    void setUp() {
        utilizador = new Utilizador();
        utilizador.setId(9L);
        utilizador.setNome("Administrador");

        tecnico = new Tecnico();
        tecnico.setId(1L);
        tecnico.setCargaTrabalhoAtual(0);
        Utilizador userTecnico = new Utilizador();
        userTecnico.setId(12L);
        userTecnico.setNome("Joao Tecnico");
        tecnico.setUtilizador(userTecnico);

        ticket = new Ticket();
        ticket.setTitulo("Problema de teste");
        ticket.setDescricao("Descrição de teste");
        ticket.setPrioridade("MEDIA");
        ticket.setTipo("INCIDENTE");
        ticket.setAbertoPor(utilizador);
    }

    // ✅ TESTE 1: Criar ticket com atribuição
    @Test
    void testCreateTicket_WithAssignment() {
        when(assignmentService.assignTechnician(any(Ticket.class))).thenReturn(tecnico);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket created = ticketService.createTicket(ticket);

        assertNotNull(created);
        assertEquals("ABERTO", created.getEstado());
        verify(ticketRepository).save(any(Ticket.class));
        verify(assignmentService).assignTechnician(any(Ticket.class));
        verify(auditService).logTicketCreation(anyLong(), anyString(), anyLong(), anyString());
    }

    // ✅ TESTE 2: Criar ticket sem técnico disponível
    @Test
    void testCreateTicket_WithoutTechnician() {
        when(assignmentService.assignTechnician(any(Ticket.class))).thenReturn(null);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket created = ticketService.createTicket(ticket);

        assertNotNull(created);
        assertNull(created.getTecnico());
        verify(ticketRepository).save(any(Ticket.class));
        verify(auditService).logTicketCreation(anyLong(), anyString(), anyLong(), anyString());
    }

    // ✅ TESTE 3: Atualizar estado do ticket
    @Test
    void testUpdateEstado() {
        ticket.setId(1L);
        ticket.setEstado("ABERTO");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket updated = ticketService.updateEstado(1L, "EM_CURSO");

        assertNotNull(updated);
        assertEquals("EM_CURSO", updated.getEstado());
        verify(ticketRepository).save(any(Ticket.class));
        verify(auditService).logTicketStateChange(anyLong(), anyString(), anyString(), anyLong());
    }

    // ✅ TESTE 4: Fechar ticket reduz carga do técnico
    @Test
    void testUpdateEstado_ToFechado_ReducesCarga() {
        ticket.setId(1L);
        ticket.setEstado("EM_CURSO");
        ticket.setTecnico(tecnico);
        tecnico.setCargaTrabalhoAtual(1);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(tecnicoRepository.save(any(Tecnico.class))).thenReturn(tecnico);

        Ticket updated = ticketService.updateEstado(1L, "FECHADO");

        assertNotNull(updated);
        assertEquals("FECHADO", updated.getEstado());
        assertEquals(0, tecnico.getCargaTrabalhoAtual());
        verify(tecnicoRepository).save(any(Tecnico.class));
    }

    // ✅ TESTE 5: Buscar ticket por ID - não encontrado
    @Test
    void testFindById_NotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        Ticket found = ticketService.findById(99L);

        assertNull(found);
    }

    // ✅ TESTE 6: Deletar ticket reduz carga do técnico
    @Test
    void testDeleteTicket_ReducesCarga() {
        ticket.setId(1L);
        ticket.setTecnico(tecnico);
        tecnico.setCargaTrabalhoAtual(1);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ticketService.delete(1L);

        assertEquals(0, tecnico.getCargaTrabalhoAtual());
        verify(ticketRepository).deleteById(1L);
        verify(tecnicoRepository).save(any(Tecnico.class));
    }
}