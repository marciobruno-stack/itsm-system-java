package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.TecnicoRepository;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final CacheService cacheService;

    public TecnicoService(TecnicoRepository tecnicoRepository,
                          UtilizadorRepository utilizadorRepository,
                          CacheService cacheService) {
        this.tecnicoRepository = tecnicoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.cacheService = cacheService;
    }

    public List<Tecnico> findAll() {
        return tecnicoRepository.findAll();
    }

    public Tecnico findById(Long id) {
        return tecnicoRepository.findById(id).orElse(null);
    }

    public Tecnico findByUtilizadorId(Long utilizadorId) {
        return tecnicoRepository.findByUtilizadorId(utilizadorId).orElse(null);
    }

    public List<Tecnico> findByOrderByCargaTrabalho() {
        return tecnicoRepository.findByOrderByCargaTrabalhoAtualAsc();
    }

    @Transactional
    public Tecnico create(Tecnico tecnico) {
        if (tecnico.getUtilizador() == null || tecnico.getUtilizador().getId() == null) {
            throw new RuntimeException("Utilizador é obrigatório");
        }
        Utilizador utilizador = utilizadorRepository.findById(tecnico.getUtilizador().getId())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        tecnico.setUtilizador(utilizador);
        tecnico.setCargaTrabalhoAtual(0);
        Tecnico saved = tecnicoRepository.save(tecnico);
        cacheService.loadTecnicos();
        return saved;
    }

    @Transactional
    public Tecnico update(Long id, Tecnico tecnico) {
        Tecnico existing = tecnicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
        existing.setDisponibilidade(tecnico.getDisponibilidade());
        existing.setCompetencias(tecnico.getCompetencias());
        Tecnico updated = tecnicoRepository.save(existing);
        cacheService.loadTecnicos();
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        tecnicoRepository.deleteById(id);
        cacheService.loadTecnicos();
    }
}