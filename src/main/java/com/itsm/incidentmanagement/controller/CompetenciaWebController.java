package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Competencia;
import com.itsm.incidentmanagement.repository.CompetenciaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/competencias")
public class CompetenciaWebController {

    private final CompetenciaRepository competenciaRepository;

    public CompetenciaWebController(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
        System.out.println("✅ CompetenciaWebController INICIALIZADO!");
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("competencias", competenciaRepository.findAll());
        return "admin/competencias";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("competencia", new Competencia());
        return "admin/competencia-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Competencia competencia, RedirectAttributes redirect) {
        try {
            competenciaRepository.save(competencia);
            redirect.addFlashAttribute("success", "Competência criada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/competencias";
    }

    @GetMapping("/delete/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            competenciaRepository.deleteById(id);
            redirect.addFlashAttribute("success", "Competência removida!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/competencias";
    }
}