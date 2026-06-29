package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketAssignmentServiceUnitTest {

    @Mock
    private CacheService cacheService;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketAssignmentService assignmentService;

    private List<Tecnico> tecnicos;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        tecnicos = new ArrayList<>();

        // Joao Tecnico
        Tecnico joao = new Tecnico();
        joao.setId(1L);
        joao.setCargaTrabalhoAtual(0);
        Utilizador userJoao = new Utilizador();
        userJoao.setId(12L);
        userJoao.setNome("Joao Tecnico");
        joao.setUtilizador(userJoao);
        Set<Competencia> compsJoao = new HashSet<>();
        compsJoao.add(new Competencia("PostgreSQL"));
        compsJoao.add(new Competencia("Servidores"));
        joao.setCompetencias(compsJoao);
        joao.setDisponibilidade("{\"segunda\":[\"09:00-18:00\"]}");
        tecnicos.add(joao);

        // Maria Tecnico
        Tecnico maria = new Tecnico();
        maria.setId(2L);
        maria.setCargaTrabalhoAtual(0);
        Utilizador userMaria = new Utilizador();
        userMaria.setId(13L);
        userMaria.setNome("Maria Tecnico");
        maria.setUtilizador(userMaria);
        Set<Competencia> compsMaria = new HashSet<>();
        compsMaria.add(new Competencia("Redes"));
        compsMaria.add(new Competencia("Linux"));
        maria.setCompetencias(compsMaria);
        maria.setDisponibilidade("{\"segunda\":[\"09:00-18:00\"]}");
        tecnicos.add(maria);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitulo("Problema de rede");
        ticket.setDescricao("Acesso à internet intermitente");
    }

    @Test
    void testAssignTechnician_ByCompetence() {
        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNotNull(escolhido);
        assertEquals("Maria Tecnico", escolhido.getUtilizador().getNome());
        verify(cacheService).updateTecnico(escolhido);
    }

    @Test
    void testAssignTechnician_NoMatchingCompetence() {
        ticket.setTitulo("Problema de impressora");
        ticket.setDescricao("A impressora não imprime");

        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNull(escolhido);
        verify(cacheService, never()).updateTecnico(any());
    }

    @Test
    void testAssignTechnician_NoTechnicians() {
        when(cacheService.getAllTecnicos()).thenReturn(new ArrayList<>());

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNull(escolhido);
        verify(cacheService, never()).updateTecnico(any());
    }

    @Test
    void testAssignTechnician_WithCarga() {
        tecnicos.get(0).setCargaTrabalhoAtual(1); // Joao com carga 1
        tecnicos.get(1).setCargaTrabalhoAtual(0); // Maria com carga 0

        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNotNull(escolhido);
        assertEquals("Maria Tecnico", escolhido.getUtilizador().getNome());
        verify(cacheService).updateTecnico(escolhido);
    }
}