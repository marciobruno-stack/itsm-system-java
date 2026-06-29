package com.itsm.incidentmanagement.security.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.repository.UtilizadorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizadorRepository utilizadorRepository;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Buscando utilizador: " + username);

        Utilizador utilizador = utilizadorRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("❌ Utilizador não encontrado: " + username);
                    return new UsernameNotFoundException("Utilizador não encontrado: " + username);
                });

        System.out.println("✅ Utilizador encontrado: " + username + " | Role: " + utilizador.getRole());
        System.out.println("🔑 Hash da password: " + utilizador.getPasswordHash());

        return new CustomUserDetails(utilizador);
    }
}