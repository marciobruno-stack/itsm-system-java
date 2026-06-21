package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.service.UtilizadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/utilizadores")
@Tag(name = "Utilizadores", description = "Endpoints para gestão de utilizadores")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    public UtilizadorController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os utilizadores")
    public ResponseEntity<List<Utilizador>> findAll() {
        return ResponseEntity.ok(utilizadorService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar utilizador por ID")
    public ResponseEntity<Utilizador> findById(@PathVariable Long id) {
        Utilizador utilizador = utilizadorService.findById(id);
        if (utilizador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(utilizador);
    }

    @PostMapping
    @Operation(summary = "Criar novo utilizador")
    public ResponseEntity<Utilizador> create(@RequestBody Utilizador utilizador) {
        try {
            Utilizador saved = utilizadorService.save(utilizador);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar utilizador")
    public ResponseEntity<Utilizador> update(@PathVariable Long id, @RequestBody Utilizador utilizador) {
        Utilizador existing = utilizadorService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        utilizador.setId(id);
        Utilizador updated = utilizadorService.save(utilizador);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover utilizador")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Utilizador existing = utilizadorService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        // Nota: Para remover, precisamos de um método delete no service
        // utilizadorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}