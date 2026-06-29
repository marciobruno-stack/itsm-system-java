package com.itsm.incidentmanagement.repository;

import com.itsm.incidentmanagement.model.entity.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {
}