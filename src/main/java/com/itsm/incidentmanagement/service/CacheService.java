package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.repository.TecnicoRepository;
import com.itsm.incidentmanagement.repository.TicketRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final TecnicoRepository tecnicoRepository;
    private final TicketRepository ticketRepository;
    private final ConcurrentHashMap<Long, Tecnico> tecnicosCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Ticket> ticketsCache = new ConcurrentHashMap<>();

    public CacheService(TecnicoRepository tecnicoRepository, TicketRepository ticketRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.ticketRepository = ticketRepository;
    }

    @PostConstruct
    public void loadCache() {
        loadTecnicos();
        loadTickets();
        System.out.println("📦 Cache carregado: " + tecnicosCache.size() + " técnicos, " + ticketsCache.size() + " tickets");
    }

    public void loadTecnicos() {
        tecnicosCache.clear();
        tecnicoRepository.findAll().forEach(t -> {
            tecnicosCache.put(t.getId(), t);
            System.out.println("   ✅ Técnico carregado: " + t.getUtilizador().getNome() +
                    " | Competências: " + t.getCompetencias().size());
        });
    }

    public void loadTickets() {
        ticketsCache.clear();
        ticketRepository.findAll().forEach(t -> ticketsCache.put(t.getId(), t));
    }

    public Tecnico getTecnico(Long id) {
        return tecnicosCache.get(id);
    }

    public List<Tecnico> getAllTecnicos() {
        return List.copyOf(tecnicosCache.values());
    }

    public void updateTecnico(Tecnico tecnico) {
        tecnicosCache.put(tecnico.getId(), tecnico);
    }

    public void updateTicket(Ticket ticket) {
        ticketsCache.put(ticket.getId(), ticket);
    }
}