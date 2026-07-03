package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Ativo;
import com.itsm.incidentmanagement.repository.AtivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AtivoService {
    private final AtivoRepository ativoRepository;

    public AtivoService(AtivoRepository ativoRepository) {
        this.ativoRepository = ativoRepository;
    }

    public List<Ativo> findAll() {
        return ativoRepository.findAll();
    }

    public Ativo findById(Long id) {
        return ativoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Ativo create(Ativo ativo) {
        ativo.setCreatedAt(LocalDateTime.now());
        return ativoRepository.save(ativo);
    }

    @Transactional
    public Ativo update(Long id, Ativo ativo) {
        Ativo existing = ativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
        existing.setTipo(ativo.getTipo());
        existing.setNome(ativo.getNome());
        existing.setEstado(ativo.getEstado());
        existing.setDataAquisicao(ativo.getDataAquisicao());
        existing.setEspecificacoes(ativo.getEspecificacoes());
        return ativoRepository.save(existing);
    }



    @Transactional
    public void delete(Long id) {
        ativoRepository.deleteById(id);
    }

    public List<Ativo> findByEstado(String estado) {
        return ativoRepository.findByEstado(estado);
    }
}