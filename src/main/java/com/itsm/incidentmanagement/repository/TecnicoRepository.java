package com.itsm.incidentmanagement.repository;

import com.itsm.incidentmanagement.model.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    Optional<Tecnico> findByUtilizadorId(Long utilizadorId);
    List<Tecnico> findByOrderByCargaTrabalhoAtualAsc();
}