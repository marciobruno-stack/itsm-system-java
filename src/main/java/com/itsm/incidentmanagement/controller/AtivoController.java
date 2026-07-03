package com.itsm.incidentmanagement.controller;

import com.itsm.incidentmanagement.model.entity.Ativo;
import com.itsm.incidentmanagement.service.AtivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@Tag(name = "Ativos", description = "Endpoints para gestão de ativos (hardware/software)")
public class AtivoController {
    private final AtivoService ativoService;

    public AtivoController(AtivoService ativoService) {
        this.ativoService = ativoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os ativos")
    public ResponseEntity<List<Ativo>> findAll() {
        return ResponseEntity.ok(ativoService.findAll());
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar ativos por estado")
    public ResponseEntity<List<Ativo>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ativoService.findByEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ativo por ID")
    public ResponseEntity<Ativo> findById(@PathVariable Long id) {
        Ativo ativo = ativoService.findById(id);
        if (ativo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ativo);
    }

    @PostMapping
    @Operation(summary = "Criar novo ativo")
    public ResponseEntity<Ativo> create(@RequestBody Ativo ativo) {
        Ativo created = ativoService.create(ativo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ativo")
    public ResponseEntity<Ativo> update(@PathVariable Long id, @RequestBody Ativo ativo) {
        Ativo updated = ativoService.update(id, ativo);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // ⭐ CORRIGIDO - Método delete com tratamento de exceções
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover ativo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            ativoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}