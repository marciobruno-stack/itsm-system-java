package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.repository.TecnicoRepository;
import com.itsm.incidentmanagement.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CacheService {
    private final TecnicoRepository tecnicoRepository;
    private final TicketRepository ticketRepository;
    private final ConcurrentHashMap<Long, Tecnico> tecnicosCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Ticket> ticketsCache = new ConcurrentHashMap<>();
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    public CacheService(TecnicoRepository tecnicoRepository, TicketRepository ticketRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.ticketRepository = ticketRepository;
    }

    private void ensureLoaded() {
        if (loaded.compareAndSet(false, true)) {
            loadTecnicos();
            loadTickets();
            System.out.println("Cache carregado: " + tecnicosCache.size() + " técnicos, " + ticketsCache.size() + " tickets");
        }
    }

    public void loadTecnicos() {
        tecnicosCache.clear();
        tecnicoRepository.findAll().forEach(t -> tecnicosCache.put(t.getId(), t));
    }

    public void loadTickets() {
        ticketsCache.clear();
        ticketRepository.findAll().forEach(t -> ticketsCache.put(t.getId(), t));
    }

    public Tecnico getTecnico(Long id) {
        ensureLoaded();
        return tecnicosCache.get(id);
    }

    public List<Tecnico> getAllTecnicos() {
        ensureLoaded();
        return List.copyOf(tecnicosCache.values());
    }

    public void updateTecnico(Tecnico tecnico) {
        ensureLoaded();
        tecnicosCache.put(tecnico.getId(), tecnico);
    }

    public void updateTicket(Ticket ticket) {
        ensureLoaded();
        ticketsCache.put(ticket.getId(), ticket);
    }
}