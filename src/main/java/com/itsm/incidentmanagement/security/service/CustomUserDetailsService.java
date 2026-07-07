package com.itsm.incidentmanagement.security.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UtilizadorRepository utilizadorRepository;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
        logger.info("CustomUserDetailsService inicializado!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Buscando utilizador: {}", username);

        Utilizador utilizador = utilizadorRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Utilizador não encontrado: {}", username);
                    return new UsernameNotFoundException("Utilizador não encontrado: " + username);
                });

        logger.debug("Utilizador encontrado: {} | Role: {}", username, utilizador.getRole());

        // Forçar remoção de caracteres especiais do hash
        String cleanHash = utilizador.getPasswordHash().replace("\\", "").trim();
        utilizador.setPasswordHash(cleanHash);
        logger.debug("Password hash limpo para utilizador: {}", username);

        return new CustomUserDetails(utilizador);
    }
}