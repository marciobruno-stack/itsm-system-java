package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.service.TecnicoService;
import com.itsm.incidentmanagement.service.TicketService;
import com.itsm.incidentmanagement.service.UtilizadorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DashboardController {

    private final TicketService ticketService;
    private final TecnicoService tecnicoService;
    private final UtilizadorService utilizadorService;

    public DashboardController(TicketService ticketService,
                               TecnicoService tecnicoService,
                               UtilizadorService utilizadorService) {
        this.ticketService = ticketService;
        this.tecnicoService = tecnicoService;
        this.utilizadorService = utilizadorService;
    }

    // ==================== DASHBOARD UTILIZADOR ====================

    @GetMapping("/utilizador/dashboard")
    public String utilizadorDashboard(Model model, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("role", "UTILIZADOR");

        if (user != null) {
            List<Ticket> meusTickets = ticketService.findByAbertoPorId(user.getId());
            model.addAttribute("meusTickets", meusTickets);
            model.addAttribute("totalTickets", meusTickets.size());
        }
        return "utilizador/dashboard";
    }

    @GetMapping("/utilizador/tickets")
    public String utilizadorTickets(Model model, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
        if (user != null) {
            model.addAttribute("tickets", ticketService.findByAbertoPorId(user.getId()));
        }
        model.addAttribute("username", username);
        return "utilizador/tickets";
    }

    @GetMapping("/utilizador/new-ticket")
    public String newTicketForm(Model model, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) return "redirect:/login";

        model.addAttribute("ticket", new Ticket());
        model.addAttribute("tecnicos", tecnicoService.findAll());
        model.addAttribute("ativos", null);
        model.addAttribute("username", username);
        return "utilizador/new-ticket";
    }

    @PostMapping("/utilizador/tickets")
    public String createTicket(@ModelAttribute Ticket ticket,
                               @RequestParam Long abertoPorId,
                               @RequestParam(required = false) Long tecnicoId,
                               @RequestParam(required = false) Long ativoId,
                               RedirectAttributes redirect) {
        try {
            Utilizador user = utilizadorService.findById(abertoPorId);
            if (user == null) {
                redirect.addFlashAttribute("error", "Utilizador não encontrado!");
                return "redirect:/utilizador/new-ticket";
            }
            ticket.setAbertoPor(user);
            ticketService.createTicket(ticket);
            redirect.addFlashAttribute("success", "Ticket criado com sucesso!");
            return "redirect:/utilizador/tickets";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
            return "redirect:/utilizador/new-ticket";
        }
    }

    // ==================== DASHBOARD TECNICO ====================

    @GetMapping("/tecnico/dashboard")
    public String tecnicoDashboard(Model model, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
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

    @GetMapping("/tecnico/tickets")
    public String tecnicoTickets(Model model, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) return "redirect:/login";

        Utilizador user = utilizadorService.findByUsername(username);
        Tecnico tecnico = tecnicoService.findByUtilizadorId(user.getId());
        if (tecnico != null) {
            model.addAttribute("tickets", ticketService.findByTecnicoId(tecnico.getId()));
        }
        model.addAttribute("username", username);
        return "tecnico/tickets";
    }

    @PostMapping("/tecnico/tickets/{id}/update-state")
    public String updateTicketState(@PathVariable Long id, @RequestParam String estado) {
        ticketService.updateEstado(id, estado);
        return "redirect:/tecnico/tickets";
    }
}