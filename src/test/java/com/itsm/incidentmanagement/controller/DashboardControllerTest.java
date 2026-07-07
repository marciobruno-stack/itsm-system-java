package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.security.config.SecurityConfig;
import com.itsm.incidentmanagement.security.filter.JwtAuthenticationFilter;
import com.itsm.incidentmanagement.security.service.CustomUserDetailsService;
import com.itsm.incidentmanagement.security.service.JwtService;
import com.itsm.incidentmanagement.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("Testes do Dashboard Controller")
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private TicketService ticketService;
    @MockBean private TecnicoService tecnicoService;
    @MockBean private UtilizadorService utilizadorService;
    @MockBean private AtivoService ativoService;

    // MockBeans para segurança/JWT necessários para carregar o contexto de segurança
    @MockBean private JwtService jwtService;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    private Ticket ticket;
    private Tecnico tecnico;
    private Utilizador utilizador;

    @BeforeEach
    void setUp() {
        utilizador = new Utilizador();
        utilizador.setId(1L);
        utilizador.setNome("Joao Silva");
        utilizador.setUsername("joao.silva");
        utilizador.setRole("ADMIN");

        tecnico = new Tecnico();
        tecnico.setId(1L);
        tecnico.setUtilizador(utilizador);
        tecnico.setCargaTrabalhoAtual(2);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitulo("Problema com Docker");
        ticket.setEstado("ABERTO");
        ticket.setDataAbertura(LocalDateTime.now());
        ticket.setAbertoPor(utilizador);
    }

    @Test
    @DisplayName("1. Deve carregar o dashboard do admin com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadAdminDashboard() throws Exception {
        when(ticketService.findAll()).thenReturn(Arrays.asList(ticket));
        when(ticketService.findByEstado("ABERTO")).thenReturn(Arrays.asList(ticket));
        when(tecnicoService.findAll()).thenReturn(Arrays.asList(tecnico));
        when(utilizadorService.findAll()).thenReturn(Arrays.asList(utilizador));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("totalTickets", "ticketsAbertos", "totalTecnicos", "totalUsers"));
    }

    @Test
    @DisplayName("2. Deve carregar a página de tickets do admin com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadAdminTickets() throws Exception {
        when(ticketService.findAll()).thenReturn(Arrays.asList(ticket));
        when(ticketService.findByEstado("ABERTO")).thenReturn(Arrays.asList(ticket));
        when(ticketService.findByEstado("EM_CURSO")).thenReturn(Arrays.asList());
        when(ticketService.findByEstado("RESOLVIDO")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/admin/tickets"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/tickets"));
    }
}
