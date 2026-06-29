package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UtilizadorService {
    private final UtilizadorRepository utilizadorRepository;

    public UtilizadorService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    public List<Utilizador> findAll() {
        return utilizadorRepository.findAll();
    }

    public Utilizador findById(Long id) {
        return utilizadorRepository.findById(id).orElse(null);
    }

    public Utilizador findByUsername(String username) {
        return utilizadorRepository.findByUsername(username).orElse(null);
    }

    public Utilizador findByEmail(String email) {
        return utilizadorRepository.findByEmail(email).orElse(null);
    }

    // ✅ NOVO MÉTODO
    public List<Utilizador> findAllByRole(String role) {
        return utilizadorRepository.findAllByRole(role);
    }

    @Transactional
    public Utilizador save(Utilizador utilizador) {
        return utilizadorRepository.save(utilizador);
    }

    // ✅ NOVO MÉTODO
    @Transactional
    public void delete(Long id) {
        utilizadorRepository.deleteById(id);
    }
}