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
        System.out.println("📋 Listando competências...");
        model.addAttribute("competencias", competenciaRepository.findAll());
        return "admin/competencias";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        System.out.println("📝 Nova competência...");
        model.addAttribute("competencia", new Competencia());
        return "admin/competencia-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Competencia competencia, RedirectAttributes redirect) {
        try {
            System.out.println("💾 Salvando: " + competencia.getNome());
            competenciaRepository.save(competencia);
            redirect.addFlashAttribute("success", "Competência criada com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/competencias";
    }

    @GetMapping("/delete/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            System.out.println("🗑️ Removendo ID: " + id);
            competenciaRepository.deleteById(id);
            redirect.addFlashAttribute("success", "Competência removida!");
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            redirect.addFlashAttribute("error", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/competencias";
    }
}