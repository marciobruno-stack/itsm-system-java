package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import com.itsm.incidentmanagement.repository.TecnicoRepository;
import com.itsm.incidentmanagement.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UtilizadorService {
    private final UtilizadorRepository utilizadorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TicketRepository ticketRepository;

    public UtilizadorService(UtilizadorRepository utilizadorRepository,
                             TecnicoRepository tecnicoRepository,
                             TicketRepository ticketRepository) {
        this.utilizadorRepository = utilizadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public List<Utilizador> findAll() {
        return utilizadorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Utilizador findById(Long id) {
        return utilizadorRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Utilizador findByUsername(String username) {
        return utilizadorRepository.findByUsername(username).orElse(null);
    }

    @Transactional(readOnly = true)
    public Utilizador findByEmail(String email) {
        return utilizadorRepository.findByEmail(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Utilizador> findAllByRole(String role) {
        return utilizadorRepository.findAllByRole(role);
    }

    @Transactional
    public Utilizador save(Utilizador utilizador) {
        try {
            return utilizadorRepository.save(utilizador);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar utilizador: " + e.getMessage());
        }
    }

    // ⭐ CORRIGIDO - Verifica se o utilizador pode ser removido
    @Transactional
    public void delete(Long id) {
        try {
            Utilizador utilizador = findById(id);
            if (utilizador == null) {
                throw new RuntimeException("Utilizador com ID " + id + " não encontrado");
            }

            // Verificar se o utilizador é um técnico
            boolean isTecnico = tecnicoRepository.findByUtilizadorId(id).isPresent();
            if (isTecnico) {
                throw new RuntimeException("Não é possível remover este utilizador pois ele está associado a um técnico. Remova o técnico primeiro.");
            }

            // Verificar se o utilizador tem tickets criados
            List<com.itsm.incidentmanagement.model.entity.Ticket> tickets = ticketRepository.findByAbertoPorId(id);
            if (!tickets.isEmpty()) {
                throw new RuntimeException("Não é possível remover este utilizador pois ele tem " + tickets.size() + " ticket(s) associado(s). Remova os tickets primeiro.");
            }

            utilizadorRepository.deleteById(id);
            System.out.println("✅ Utilizador " + id + " removido com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover utilizador " + id + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}