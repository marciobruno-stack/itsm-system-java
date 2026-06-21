package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Competencia;
import com.itsm.incidentmanagement.repository.CompetenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CompetenciaService {
    private final CompetenciaRepository competenciaRepository;

    public CompetenciaService(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    public List<Competencia> findAll() {
        return competenciaRepository.findAll();
    }

    public Competencia findById(Long id) {
        return competenciaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Competencia create(Competencia competencia) {
        return competenciaRepository.save(competencia);
    }

    @Transactional
    public Competencia update(Long id, Competencia competencia) {
        Competencia existing = competenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competência não encontrada"));
        existing.setNome(competencia.getNome());
        return competenciaRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        competenciaRepository.deleteById(id);
    }
}