package com.itsm.incidentmanagement.repository;

import com.itsm.incidentmanagement.model.entity.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    List<Ativo> findByEstado(String estado);
}