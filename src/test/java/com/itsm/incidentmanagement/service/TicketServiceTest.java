package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private TicketAssignmentService ticketAssignmentService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private Tecnico tecnico1;
    private Tecnico tecnico2;
    private Utilizador utilizador;

    @BeforeEach
    void setUp() {
        utilizador = new Utilizador();
        utilizador.setId(1L);
        utilizador.setUsername("teste");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitulo("Teste");
        ticket.setDescricao("Descrição");
        ticket.setEstado("ABERTO");
        ticket.setPrioridade("ALTA");
        ticket.setTipo("INCIDENTE");
        ticket.setAbertoPor(utilizador);

        tecnico1 = new Tecnico();
        tecnico1.setId(1L);
        tecnico1.setDisponibilidade("DISPONIVEL");
        tecnico1.setCargaTrabalhoAtual(0);
        tecnico1.setUtilizador(utilizador);

        tecnico2 = new Tecnico();
        tecnico2.setId(2L);
        tecnico2.setDisponibilidade("DISPONIVEL");
        tecnico2.setCargaTrabalhoAtual(5);
        tecnico2.setUtilizador(utilizador);
    }

    @Test
    void testCreateTicket() {
        when(ticketAssignmentService.assignTechnician(any(Ticket.class))).thenReturn(tecnico1);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.createTicket(ticket);

        assertNotNull(result);
        assertEquals("ABERTO", result.getEstado());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testAtribuirTecnicoMenorCarga() {
        when(ticketAssignmentService.assignTechnician(any(Ticket.class))).thenReturn(tecnico1);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.createTicket(ticket);

        assertNotNull(result);
        // O técnico com menor carga é incrementado em 1 na criação do ticket
        assertEquals(1, tecnico1.getCargaTrabalhoAtual());
        assertTrue(tecnico1.getCargaTrabalhoAtual() < tecnico2.getCargaTrabalhoAtual());
    }

    @Test
    void testUpdateEstado() {
        Ticket ticketComTecnico = new Ticket();
        ticketComTecnico.setId(1L);
        ticketComTecnico.setTitulo("Teste");
        ticketComTecnico.setEstado("ABERTO");
        ticketComTecnico.setTecnico(tecnico1);
        tecnico1.setCargaTrabalhoAtual(1);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketComTecnico));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketComTecnico);

        Ticket result = ticketService.updateEstado(1L, "RESOLVIDO");

        assertNotNull(result);
        assertEquals("RESOLVIDO", result.getEstado());
    }

    @Test
    void testFindByEstado() {
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketRepository.findByEstado("ABERTO")).thenReturn(tickets);

        List<Ticket> result = ticketService.findByEstado("ABERTO");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABERTO", result.get(0).getEstado());
    }
}
