package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {

    private final TicketService ticketService;
    private final TecnicoService tecnicoService;
    private final UtilizadorService utilizadorService;
    private final CompetenciaService competenciaService;

    public WebController(TicketService ticketService,
                         TecnicoService tecnicoService,
                         UtilizadorService utilizadorService,
                         CompetenciaService competenciaService) {
        this.ticketService = ticketService;
        this.tecnicoService = tecnicoService;
        this.utilizadorService = utilizadorService;
        this.competenciaService = competenciaService;
        System.out.println("✅ WebController carregado com sucesso!");
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        System.out.println("📊 A carregar dashboard admin...");

        model.addAttribute("username", "Admin");
        model.addAttribute("role", "ADMIN");
        model.addAttribute("totalTickets", ticketService.findAll().size());
        model.addAttribute("ticketsAbertos", ticketService.findByEstado("ABERTO").size());
        model.addAttribute("totalTecnicos", tecnicoService.findAll().size());
        model.addAttribute("totalUsers", utilizadorService.findAll().size());
        model.addAttribute("totalCompetencias", competenciaService.findAll().size());

        List<Ticket> recentTickets = ticketService.findAll();
        if (recentTickets.size() > 5) {
            recentTickets = recentTickets.subList(0, 5);
        }
        model.addAttribute("recentTickets", recentTickets);

        return "admin/dashboard";
    }

    // ==================== UTILIZADORES ====================
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", utilizadorService.findAll());
        return "admin/users";
    }

    @GetMapping("/admin/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new Utilizador());
        return "admin/user-form";
    }

    @PostMapping("/admin/users")
    public String createUser(@ModelAttribute Utilizador user, RedirectAttributes redirect) {
        try {
            user.setPasswordHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
            utilizadorService.save(user);
            redirect.addFlashAttribute("success", "Utilizador criado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        utilizadorService.delete(id);
        redirect.addFlashAttribute("success", "Utilizador removido!");
        return "redirect:/admin/users";
    }

    // ==================== TÉCNICOS ====================
    @GetMapping("/admin/technicians")
    public String listTechnicians(Model model) {
        model.addAttribute("technicians", tecnicoService.findAll());
        model.addAttribute("users", utilizadorService.findAll());
        return "admin/technicians";
    }

    @GetMapping("/admin/technicians/new")
    public String newTechnicianForm(Model model) {
        model.addAttribute("tecnico", new Tecnico());
        model.addAttribute("users", utilizadorService.findAll());
        return "admin/technician-form";
    }

    @PostMapping("/admin/technicians")
    public String createTechnician(@ModelAttribute Tecnico tecnico,
                                   @RequestParam Long utilizadorId,
                                   RedirectAttributes redirect) {
        try {
            Utilizador user = utilizadorService.findById(utilizadorId);
            if (user == null) {
                redirect.addFlashAttribute("error", "Utilizador não encontrado!");
                return "redirect:/admin/technicians/new";
            }
            tecnico.setUtilizador(user);
            tecnico.setCargaTrabalhoAtual(0);
            tecnicoService.create(tecnico);
            redirect.addFlashAttribute("success", "Técnico criado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/technicians";
    }

    @GetMapping("/admin/technicians/delete/{id}")
    public String deleteTechnician(@PathVariable Long id, RedirectAttributes redirect) {
        tecnicoService.delete(id);
        redirect.addFlashAttribute("success", "Técnico removido!");
        return "redirect:/admin/technicians";
    }

    // ==================== TICKETS ====================
    @GetMapping("/admin/tickets")
    public String listTickets(Model model) {
        model.addAttribute("tickets", ticketService.findAll());
        return "admin/tickets";
    }

    @GetMapping("/admin/tickets/new")
    public String newTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("users", utilizadorService.findAll());
        model.addAttribute("tecnicos", tecnicoService.findAll());
        return "admin/ticket-form";
    }

    @PostMapping("/admin/tickets")
    public String createTicket(@ModelAttribute Ticket ticket,
                               @RequestParam Long abertoPorId,
                               @RequestParam(required = false) Long tecnicoId,
                               RedirectAttributes redirect) {
        try {
            System.out.println("📝 Criando ticket...");
            System.out.println("Título: " + ticket.getTitulo());
            System.out.println("Descrição: " + ticket.getDescricao());
            System.out.println("Prioridade: " + ticket.getPrioridade());

            Utilizador user = utilizadorService.findById(abertoPorId);
            if (user == null) {
                redirect.addFlashAttribute("error", "Utilizador não encontrado!");
                return "redirect:/admin/tickets/new";
            }

            ticket.setAbertoPor(user);
            ticket.setEstado("ABERTO");

            if (ticket.getTipo() == null || ticket.getTipo().isEmpty()) {
                ticket.setTipo("INCIDENTE");
                System.out.println("⚠️ Tipo definido como INCIDENTE (padrão)");
            }

            if (tecnicoId != null && tecnicoId > 0) {
                Tecnico tecnico = tecnicoService.findById(tecnicoId);
                ticket.setTecnico(tecnico);
                System.out.println("Técnico associado: " + tecnico.getId());
            }

            ticketService.createTicket(ticket);
            System.out.println("✅ Ticket criado com sucesso! ID: " + ticket.getId());
            redirect.addFlashAttribute("success", "Ticket criado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar ticket: " + e.getMessage());
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
    }

    @PostMapping("/admin/tickets/{id}/update-state")
    public String updateTicketState(@PathVariable Long id, @RequestParam String estado, RedirectAttributes redirect) {
        try {
            ticketService.updateEstado(id, estado);
            redirect.addFlashAttribute("success", "Estado atualizado!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
    }

    @GetMapping("/admin/tickets/delete/{id}")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirect) {
        ticketService.delete(id);
        redirect.addFlashAttribute("success", "Ticket removido!");
        return "redirect:/admin/tickets";
    }
}