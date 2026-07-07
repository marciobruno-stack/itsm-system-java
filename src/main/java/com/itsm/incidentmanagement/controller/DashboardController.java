package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final TicketService ticketService;
    private final TecnicoService tecnicoService;
    private final UtilizadorService utilizadorService;
    private final AtivoService ativoService;

    public DashboardController(TicketService ticketService,
                               TecnicoService tecnicoService,
                               UtilizadorService utilizadorService,
                               AtivoService ativoService) {
        this.ticketService = ticketService;
        this.tecnicoService = tecnicoService;
        this.utilizadorService = utilizadorService;
        this.ativoService = ativoService;
    }

    // ==================== ADMIN DASHBOARD ====================

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        String username = getAuthenticatedUsername();
        model.addAttribute("username", username != null ? username : "Admin");
        model.addAttribute("role", "ADMIN");
        model.addAttribute("totalTickets", ticketService.findAll().size());
        model.addAttribute("ticketsAbertos", ticketService.findByEstado("ABERTO").size());
        model.addAttribute("totalTecnicos", tecnicoService.findAll().size());
        model.addAttribute("totalUsers", utilizadorService.findAll().size());

        List<Ticket> recentTickets = ticketService.findAll();
        if (recentTickets.size() > 5) {
            recentTickets = recentTickets.subList(0, 5);
        }
        model.addAttribute("recentTickets", recentTickets);
        return "admin/dashboard";
    }

    // ==================== ADMIN TICKETS ====================

    @GetMapping("/admin/tickets")
    public String adminTickets(Model model) {
        String username = getAuthenticatedUsername();
        List<Ticket> allTickets = ticketService.findAll();

        model.addAttribute("username", username != null ? username : "Admin");
        model.addAttribute("tickets", allTickets);
        model.addAttribute("totalTickets", allTickets.size());
        model.addAttribute("abertos", ticketService.findByEstado("ABERTO").size());
        model.addAttribute("emCurso", ticketService.findByEstado("EM_CURSO").size());
        model.addAttribute("resolvidos", ticketService.findByEstado("RESOLVIDO").size());

        return "admin/tickets";
    }

    // ==================== TECNICO DASHBOARD ====================

    @GetMapping("/tecnico/dashboard")
    public String tecnicoDashboard(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
        if (user == null) return "redirect:/login";

        Tecnico tecnico = tecnicoService.findByUtilizadorId(user.getId());
        model.addAttribute("username", username);
        model.addAttribute("role", "TECNICO");

        if (tecnico != null) {
            List<Ticket> meusTickets = ticketService.findByTecnicoId(tecnico.getId());
            model.addAttribute("meusTickets", meusTickets);
            model.addAttribute("totalTickets", meusTickets.size());
            model.addAttribute("cargaTrabalho", tecnico.getCargaTrabalhoAtual());
        }
        return "tecnico/dashboard";
    }

    // ==================== TECNICO TICKETS ====================

    @GetMapping("/tecnico/tickets")
    public String tecnicoTickets(Model model) {
        logger.debug("A carregar /tecnico/tickets");

        String username = getAuthenticatedUsername();
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
        if (user == null) return "redirect:/login";

        Tecnico tecnico = tecnicoService.findByUtilizadorId(user.getId());
        model.addAttribute("username", username);
        model.addAttribute("role", "TECNICO");

        if (tecnico != null) {
            List<Ticket> tickets = ticketService.findByTecnicoId(tecnico.getId());
            model.addAttribute("tickets", tickets);
            model.addAttribute("totalTickets", tickets.size());
            logger.debug("Tickets encontrados: {}", tickets.size());
        } else {
            model.addAttribute("tickets", new ArrayList<>());
            model.addAttribute("totalTickets", 0);
            logger.debug("Técnico não encontrado para: {}", username);
        }

        return "tecnico/tickets";
    }

    // ==================== TECNICO ATUALIZAR ESTADO ====================

    @PostMapping("/tecnico/tickets/update-state")
    public String updateTicketState(@RequestParam Long ticketId,
                                    @RequestParam String estado,
                                    RedirectAttributes redirect) {
        try {
            logger.debug("Atualizando ticket {} para estado: {}", ticketId, estado);
            ticketService.updateEstado(ticketId, estado);
            redirect.addFlashAttribute("success", "Estado atualizado com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao atualizar ticket: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/tecnico/dashboard";
    }

    // ==================== UTILIZADOR DASHBOARD ====================

    @GetMapping("/utilizador/dashboard")
    public String utilizadorDashboard(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
        if (user == null) return "redirect:/login";

        model.addAttribute("username", username);
        model.addAttribute("role", "UTILIZADOR");

        List<Ticket> meusTickets = ticketService.findByAbertoPorId(user.getId());
        model.addAttribute("meusTickets", meusTickets);
        model.addAttribute("totalTickets", meusTickets.size());

        long abertos = meusTickets.stream()
                .filter(t -> "ABERTO".equals(t.getEstado()) || "ATRIBUIDO".equals(t.getEstado()))
                .count();
        long emAndamento = meusTickets.stream()
                .filter(t -> "EM_CURSO".equals(t.getEstado()))
                .count();

        model.addAttribute("ticketsAbertos", abertos);
        model.addAttribute("ticketsEmAndamento", emAndamento);
        return "utilizador/dashboard";
    }

    // ==================== UTILIZADOR - NOVO TICKET ====================

    @GetMapping("/utilizador/new-ticket")
    public String newTicketForm(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) return "redirect:/login";

        logger.debug("A carregar formulário de novo ticket para: {}", username);

        model.addAttribute("username", username);
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("tecnicos", tecnicoService.findAll());
        model.addAttribute("ativos", ativoService.findAll());
        return "utilizador/new-ticket";
    }

    // ==================== UTILIZADOR - CRIAR TICKET ====================

    @PostMapping("/utilizador/tickets")
    public String createTicketByUser(@ModelAttribute Ticket ticket,
                                     @RequestParam(required = false) Long tecnicoId,
                                     @RequestParam(required = false) Long ativoId,
                                     RedirectAttributes redirect) {
        try {
            String username = getAuthenticatedUsername();
            if (username == null) return "redirect:/login";

            logger.debug("A criar ticket por utilizador: {}", username);

            Utilizador user = utilizadorService.findByUsername(username);
            if (user == null) {
                redirect.addFlashAttribute("error", "Utilizador não encontrado!");
                return "redirect:/utilizador/new-ticket";
            }

            // Definir valores
            ticket.setAbertoPor(user);
            ticket.setEstado("ABERTO");

            if (ticket.getTipo() == null || ticket.getTipo().isEmpty()) {
                ticket.setTipo("INCIDENTE");
            }

            if (tecnicoId != null && tecnicoId > 0) {
                Tecnico tecnico = tecnicoService.findById(tecnicoId);
                ticket.setTecnico(tecnico);
            }

            if (ativoId != null && ativoId > 0) {
                Ativo ativo = ativoService.findById(ativoId);
                ticket.setAtivo(ativo);
            }

            // Criar o ticket (com atribuição automática)
            Ticket created = ticketService.createTicket(ticket);

            logger.debug("Ticket #{} criado com sucesso", created.getId());
            redirect.addFlashAttribute("success", "Ticket #" + created.getId() + " criado com sucesso!");
            return "redirect:/utilizador/tickets";

        } catch (Exception e) {
            logger.error("Erro ao criar ticket: {}", e.getMessage(), e);
            redirect.addFlashAttribute("error", "Erro ao criar ticket: " + e.getMessage());
            return "redirect:/utilizador/new-ticket";
        }
    }

    // ==================== UTILIZADOR - MEUS TICKETS ====================

    @GetMapping("/utilizador/tickets")
    public String utilizadorTickets(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) return "redirect:/login";

        logger.debug("A carregar tickets do utilizador: {}", username);

        Utilizador user = utilizadorService.findByUsername(username);
        if (user == null) return "redirect:/login";

        List<Ticket> meusTickets = ticketService.findByAbertoPorId(user.getId());

        model.addAttribute("username", username);
        model.addAttribute("role", "UTILIZADOR");
        model.addAttribute("tickets", meusTickets);
        model.addAttribute("totalTickets", meusTickets.size());

        long abertos = meusTickets.stream()
                .filter(t -> "ABERTO".equals(t.getEstado()) || "ATRIBUIDO".equals(t.getEstado()))
                .count();
        long emAndamento = meusTickets.stream()
                .filter(t -> "EM_CURSO".equals(t.getEstado()))
                .count();

        model.addAttribute("ticketsAbertos", abertos);
        model.addAttribute("ticketsEmAndamento", emAndamento);

        logger.debug("Total de tickets: {}", meusTickets.size());
        return "utilizador/tickets";
    }

    // ==================== MÉTODO AUXILIAR ====================

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }
}