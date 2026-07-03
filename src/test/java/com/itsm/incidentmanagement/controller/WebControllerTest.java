package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.security.service.CustomUserDetailsService;
import com.itsm.incidentmanagement.security.service.JwtService;
import com.itsm.incidentmanagement.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
@DisplayName("Testes do Web Controller")
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private TicketService ticketService;
    @MockBean private TecnicoService tecnicoService;
    @MockBean private UtilizadorService utilizadorService;
    @MockBean private CompetenciaService competenciaService;
    @MockBean private JwtService jwtService;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    private Utilizador utilizador;
    private Tecnico tecnico;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        utilizador = new Utilizador();
        utilizador.setId(1L);
        utilizador.setNome("João Silva");
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

    // ==================== TESTE 1: Página Inicial ====================
    @Test
    @DisplayName("1. Página inicial deve redirecionar para login")
    void shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    // ==================== TESTE 2: Listar Utilizadores ====================
    @Test
    @DisplayName("2. Deve listar todos os utilizadores com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldListUsers() throws Exception {
        when(utilizadorService.findAll()).thenReturn(Arrays.asList(utilizador));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));
    }

    // ==================== TESTE 3: Remover Utilizador ====================
    @Test
    @DisplayName("3. Deve remover um utilizador com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUser() throws Exception {
        doNothing().when(utilizadorService).delete(anyLong());

        mockMvc.perform(get("/admin/users/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));
    }

    // ==================== TESTE 4: Listar Técnicos ====================
    @Test
    @DisplayName("4. Deve listar todos os técnicos com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldListTechnicians() throws Exception {
        when(tecnicoService.findAll()).thenReturn(Arrays.asList(tecnico));
        when(utilizadorService.findAll()).thenReturn(Arrays.asList(utilizador));

        mockMvc.perform(get("/admin/technicians"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicians"));
    }

    // ==================== TESTE 5: Remover Técnico ====================
    @Test
    @DisplayName("5. Deve remover um técnico com sucesso")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteTechnician() throws Exception {
        doNothing().when(tecnicoService).delete(anyLong());

        mockMvc.perform(get("/admin/technicians/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/technicians"));
    }

    // ==================== TESTE 6: Formulário Novo Utilizador ====================
    @Test
    @DisplayName("6. Deve carregar o formulário de novo utilizador")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadNewUserForm() throws Exception {
        mockMvc.perform(get("/admin/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("user", "isEdit"));
    }

    // ==================== TESTE 7: Formulário Editar Utilizador ====================
    @Test
    @DisplayName("7. Deve carregar o formulário de edição de utilizador")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadEditUserForm() throws Exception {
        when(utilizadorService.findById(1L)).thenReturn(utilizador);

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("user", "isEdit"));
    }

    // ==================== TESTE 8: Formulário Novo Técnico ====================
    @Test
    @DisplayName("8. Deve carregar o formulário de novo técnico")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadNewTechnicianForm() throws Exception {
        when(utilizadorService.findAll()).thenReturn(Arrays.asList(utilizador));

        mockMvc.perform(get("/admin/technicians/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technician-form"))
                .andExpect(model().attributeExists("tecnico", "users"));
    }

    // ==================== TESTE 9: Formulário Novo Ticket ====================
    @Test
    @DisplayName("9. Deve carregar o formulário de novo ticket")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadNewTicketForm() throws Exception {
        when(utilizadorService.findAll()).thenReturn(Arrays.asList(utilizador));
        when(tecnicoService.findAll()).thenReturn(Arrays.asList(tecnico));

        mockMvc.perform(get("/admin/tickets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/ticket-form"))
                .andExpect(model().attributeExists("ticket", "users", "tecnicos"));
    }

    // ==================== TESTE 10: Formulário Editar Ticket ====================
    @Test
    @DisplayName("10. Deve carregar o formulário de edição de ticket")
    @WithMockUser(roles = "ADMIN")
    void shouldLoadEditTicketForm() throws Exception {
        when(ticketService.findById(1L)).thenReturn(ticket);
        when(utilizadorService.findAll()).thenReturn(Arrays.asList(utilizador));
        when(tecnicoService.findAll()).thenReturn(Arrays.asList(tecnico));

        mockMvc.perform(get("/admin/tickets/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/ticket-edit-form"))
                .andExpect(model().attributeExists("ticket", "users", "tecnicos"));
    }
}