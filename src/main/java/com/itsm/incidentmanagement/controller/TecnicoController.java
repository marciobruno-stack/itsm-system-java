package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.service.TecnicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
@Tag(name = "Técnicos", description = "Endpoints para gestão de técnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os técnicos")
    public ResponseEntity<List<Tecnico>> findAll() {
        return ResponseEntity.ok(tecnicoService.findAll());
    }

    @GetMapping("/ordenados-carga")
    @Operation(summary = "Listar técnicos ordenados por carga de trabalho")
    public ResponseEntity<List<Tecnico>> findAllOrderedByCarga() {
        return ResponseEntity.ok(tecnicoService.findByOrderByCargaTrabalho());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar técnico por ID")
    public ResponseEntity<Tecnico> findById(@PathVariable Long id) {
        Tecnico tecnico = tecnicoService.findById(id);
        if (tecnico == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tecnico);
    }

    @PostMapping
    @Operation(summary = "Criar novo técnico")
    public ResponseEntity<Tecnico> create(@RequestBody Tecnico tecnico) {
        try {
            Tecnico created = tecnicoService.create(tecnico);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar técnico")
    public ResponseEntity<Tecnico> update(@PathVariable Long id, @RequestBody Tecnico tecnico) {
        try {
            Tecnico updated = tecnicoService.update(id, tecnico);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ⭐ CORRIGIDO - Método delete com tratamento de exceções
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover técnico")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            tecnicoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}