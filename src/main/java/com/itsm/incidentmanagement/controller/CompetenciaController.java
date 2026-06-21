package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Competencia;
import com.itsm.incidentmanagement.service.CompetenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/competencias")
@Tag(name = "Competências", description = "Endpoints para gestão de competências")
public class CompetenciaController {
    private final CompetenciaService competenciaService;

    public CompetenciaController(CompetenciaService competenciaService) {
        this.competenciaService = competenciaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as competências")
    public ResponseEntity<List<Competencia>> findAll() {
        return ResponseEntity.ok(competenciaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar competência por ID")
    public ResponseEntity<Competencia> findById(@PathVariable Long id) {
        Competencia competencia = competenciaService.findById(id);
        if (competencia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(competencia);
    }

    @PostMapping
    @Operation(summary = "Criar nova competência")
    public ResponseEntity<Competencia> create(@RequestBody Competencia competencia) {
        Competencia created = competenciaService.create(competencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar competência")
    public ResponseEntity<Competencia> update(@PathVariable Long id, @RequestBody Competencia competencia) {
        Competencia updated = competenciaService.update(id, competencia);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover competência")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}