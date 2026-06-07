package com.itsm.incidentmanagement.repository;

import com.itsm.incidentmanagement.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEstado(String estado);
    List<Ticket> findByTecnicoId(Long tecnicoId);
    List<Ticket> findByAbertoPorId(Long abertoPorId);
    long countByTecnicoIdAndEstadoNot(Long tecnicoId, String estadoFechado);
}