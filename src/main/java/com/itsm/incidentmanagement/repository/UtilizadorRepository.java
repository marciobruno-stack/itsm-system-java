package com.itsm.incidentmanagement.repository;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    Optional<Utilizador> findByUsername(String username);
    Optional<Utilizador> findByEmail(String email);
}