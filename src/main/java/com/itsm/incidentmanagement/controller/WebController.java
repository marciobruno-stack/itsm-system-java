package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    private final TicketService ticketService;
    private final TecnicoService tecnicoService;
    private final UtilizadorService utilizadorService;
    private final CompetenciaService competenciaService;
    private final PasswordEncoder passwordEncoder;

    public WebController(TicketService ticketService,
                         TecnicoService tecnicoService,
                         UtilizadorService utilizadorService,
                         CompetenciaService competenciaService,
                         PasswordEncoder passwordEncoder) {
        this.ticketService = ticketService;
        this.tecnicoService = tecnicoService;
        this.utilizadorService = utilizadorService;
        this.competenciaService = competenciaService;
        this.passwordEncoder = passwordEncoder;
        logger.info("WebController carregado com sucesso");
    }

    // ==================== HOME ====================
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
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
        model.addAttribute("isEdit", false);
        return "admin/user-form";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Utilizador user = utilizadorService.findById(id);
        if (user == null) {
            model.addAttribute("error", "Utilizador não encontrado!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("isEdit", true);
        return "admin/user-form";
    }

    @PostMapping("/admin/users")
    public String createUser(@ModelAttribute Utilizador user, RedirectAttributes redirect) {
        try {
            if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                redirect.addFlashAttribute("error", "Password é obrigatória!");
                return "redirect:/admin/users/new";
            }
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            utilizadorService.save(user);
            redirect.addFlashAttribute("success", "Utilizador criado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao criar utilizador: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute Utilizador user,
                             @RequestParam(required = false) String newPassword,
                             RedirectAttributes redirect) {
        try {
            Utilizador existing = utilizadorService.findById(id);
            if (existing == null) {
                redirect.addFlashAttribute("error", "Utilizador não encontrado!");
                return "redirect:/admin/users";
            }

            existing.setUsername(user.getUsername());
            existing.setNome(user.getNome());
            existing.setEmail(user.getEmail());
            existing.setRole(user.getRole());

            if (newPassword != null && !newPassword.isEmpty()) {
                existing.setPasswordHash(passwordEncoder.encode(newPassword));
            }

            utilizadorService.save(existing);
            redirect.addFlashAttribute("success", "Utilizador atualizado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao atualizar utilizador: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            utilizadorService.delete(id);
            redirect.addFlashAttribute("success", "Utilizador removido com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao remover utilizador: " + e.getMessage());
        }
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
            redirect.addFlashAttribute("error", "Erro ao criar técnico: " + e.getMessage());
        }
        return "redirect:/admin/technicians";
    }

    @GetMapping("/admin/technicians/delete/{id}")
    public String deleteTechnician(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            tecnicoService.delete(id);
            redirect.addFlashAttribute("success", "Técnico removido com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao remover técnico: " + e.getMessage());
        }
        return "redirect:/admin/technicians";
    }

    // ==================== TICKETS (CRUD) ====================

    @GetMapping("/admin/tickets/edit/{id}")
    public String editTicketForm(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            model.addAttribute("error", "Ticket não encontrado!");
            return "redirect:/admin/tickets";
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("users", utilizadorService.findAll());
        model.addAttribute("tecnicos", tecnicoService.findAll());
        return "admin/ticket-edit-form";
    }

    @PostMapping("/admin/tickets/edit/{id}")
    public String updateTicket(@PathVariable Long id,
                               @ModelAttribute Ticket ticket,
                               @RequestParam(required = false) Long tecnicoId,
                               RedirectAttributes redirect) {
        try {
            Ticket existing = ticketService.findById(id);
            if (existing == null) {
                redirect.addFlashAttribute("error", "Ticket não encontrado!");
                return "redirect:/admin/tickets";
            }

            existing.setTitulo(ticket.getTitulo());
            existing.setDescricao(ticket.getDescricao());
            existing.setPrioridade(ticket.getPrioridade());
            existing.setTipo(ticket.getTipo());

            if (tecnicoId != null && tecnicoId > 0) {
                Tecnico tecnico = tecnicoService.findById(tecnicoId);
                if (tecnico != null) {
                    existing.setTecnico(tecnico);
                }
            } else {
                existing.setTecnico(null);
            }

            ticketService.update(id, existing);
            redirect.addFlashAttribute("success", "Ticket #" + id + " atualizado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao atualizar ticket: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
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
            Utilizador user = utilizadorService.findById(abertoPorId);
            if (user == null) {
                redirect.addFlashAttribute("error", "Utilizador não encontrado!");
                return "redirect:/admin/tickets/new";
            }

            ticket.setAbertoPor(user);
            ticket.setEstado("ABERTO");

            if (ticket.getTipo() == null || ticket.getTipo().isEmpty()) {
                ticket.setTipo("INCIDENTE");
            }

            if (tecnicoId != null && tecnicoId > 0) {
                Tecnico tecnico = tecnicoService.findById(tecnicoId);
                ticket.setTecnico(tecnico);
            }

            ticketService.createTicket(ticket);
            redirect.addFlashAttribute("success", "Ticket criado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao criar ticket: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
    }

    @PostMapping("/admin/tickets/{id}/update-state")
    public String updateTicketState(@PathVariable Long id, @RequestParam String estado, RedirectAttributes redirect) {
        try {
            ticketService.updateEstado(id, estado);
            redirect.addFlashAttribute("success", "Estado atualizado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao atualizar estado: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
    }

    @GetMapping("/admin/tickets/delete/{id}")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            ticketService.delete(id);
            redirect.addFlashAttribute("success", "Ticket removido com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao remover ticket: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
    }
}