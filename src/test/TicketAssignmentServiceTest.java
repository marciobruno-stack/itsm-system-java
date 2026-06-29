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
class TicketAssignmentServiceTest {

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
        // Criar técnicos com competências
        tecnicos = new ArrayList<>();

        // Joao Tecnico - Especialista em Servidores
        Tecnico joao = new Tecnico();
        joao.setId(1L);
        joao.setCargaTrabalhoAtual(0);
        Utilizador userJoao = new Utilizador();
        userJoao.setId(12L);
        userJoao.setNome("Joao Tecnico");
        joao.setUtilizador(userJoao);

        Set<Competencia> compsJoao = new HashSet<>();
        compsJoao.add(new Competencia("PostgreSQL"));
        compsJoao.add(new Competencia("Java"));
        compsJoao.add(new Competencia("Servidores"));
        compsJoao.add(new Competencia("Database"));
        joao.setCompetencias(compsJoao);
        joao.setDisponibilidade("{\"segunda\":[\"09:00-18:00\"]}");
        tecnicos.add(joao);

        // Maria Tecnico - Especialista em Redes
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
        compsMaria.add(new Competencia("AWS"));
        maria.setCompetencias(compsMaria);
        maria.setDisponibilidade("{\"segunda\":[\"09:00-18:00\"]}");
        tecnicos.add(maria);

        // Carlos Tecnico - Especialista em DevOps
        Tecnico carlos = new Tecnico();
        carlos.setId(3L);
        carlos.setCargaTrabalhoAtual(0);
        Utilizador userCarlos = new Utilizador();
        userCarlos.setId(14L);
        userCarlos.setNome("Carlos Tecnico");
        carlos.setUtilizador(userCarlos);

        Set<Competencia> compsCarlos = new HashSet<>();
        compsCarlos.add(new Competencia("Docker"));
        compsCarlos.add(new Competencia("Kubernetes"));
        compsCarlos.add(new Competencia("DevOps"));
        carlos.setCompetencias(compsCarlos);
        carlos.setDisponibilidade("{\"segunda\":[\"09:00-18:00\"]}");
        tecnicos.add(carlos);

        // Criar ticket
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitulo("Problema de rede");
        ticket.setDescricao("Acesso à internet intermitente");
        ticket.setPrioridade("ALTA");
        ticket.setTipo("INCIDENTE");
    }

    // ✅ TESTE 1: Atribuição por competência (Rede → Maria)
    @Test
    void testAssignTechnician_ByCompetence_Rede() {
        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNotNull(escolhido);
        assertEquals("Maria Tecnico", escolhido.getUtilizador().getNome());
        verify(cacheService).updateTecnico(escolhido);
    }

    // ✅ TESTE 2: Atribuição por competência (Servidor → Joao)
    @Test
    void testAssignTechnician_ByCompetence_Servidor() {
        ticket.setTitulo("Servidor PostgreSQL lento");
        ticket.setDescricao("Performance reduzida na base de dados");

        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNotNull(escolhido);
        assertEquals("Joao Tecnico", escolhido.getUtilizador().getNome());
        verify(cacheService).updateTecnico(escolhido);
    }

    // ✅ TESTE 3: Atribuição por competência (Docker → Carlos)
    @Test
    void testAssignTechnician_ByCompetence_Docker() {
        ticket.setTitulo("Problema com Docker");
        ticket.setDescricao("Container não está a iniciar");

        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNotNull(escolhido);
        assertEquals("Carlos Tecnico", escolhido.getUtilizador().getNome());
        verify(cacheService).updateTecnico(escolhido);
    }

    // ✅ TESTE 4: Sem técnicos disponíveis
    @Test
    void testAssignTechnician_NoTechnicians() {
        when(cacheService.getAllTecnicos()).thenReturn(new ArrayList<>());

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNull(escolhido);
        verify(cacheService, never()).updateTecnico(any());
    }

    // ✅ TESTE 5: Escolher técnico com menor carga
    @Test
    void testAssignTechnician_WithCarga() {
        // Maria com carga 1, Joao com carga 0
        tecnicos.get(0).setCargaTrabalhoAtual(0); // Joao
        tecnicos.get(1).setCargaTrabalhoAtual(1); // Maria

        ticket.setTitulo("Problema de rede");
        ticket.setDescricao("Acesso à internet intermitente");

        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNotNull(escolhido);
        assertEquals("Joao Tecnico", escolhido.getUtilizador().getNome());
        verify(cacheService).updateTecnico(escolhido);
    }

    // ✅ TESTE 6: Sem competências correspondentes
    @Test
    void testAssignTechnician_NoMatchingCompetence() {
        ticket.setTitulo("Problema de impressora");
        ticket.setDescricao("A impressora não imprime");

        when(cacheService.getAllTecnicos()).thenReturn(tecnicos);

        Tecnico escolhido = assignmentService.assignTechnician(ticket);

        assertNull(escolhido);
        verify(cacheService, never()).updateTecnico(any());
    }

    // ✅ TESTE 7: Extração de palavras-chave
    @Test
    void testExtrairPalavrasChave() {
        String texto = "Problema de rede na internet";
        // Acesso via reflection para testar método privado
        // Ou mover método para public ou protected
    }
}