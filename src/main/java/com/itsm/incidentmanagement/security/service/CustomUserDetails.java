package com.itsm.incidentmanagement.security.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);

    private final Utilizador utilizador;

    public CustomUserDetails(Utilizador utilizador) {
        this.utilizador = utilizador;
        logger.debug("CustomUserDetails criado para: {}", utilizador.getUsername());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + utilizador.getRole();
        logger.debug("GrantedAuthority: {}", role);
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        String hash = utilizador.getPasswordHash();
        if (hash != null) {
            hash = hash.replace("\\", "").trim();
        }
        logger.debug("Password hash retrieved");
        return hash;
    }

    @Override
    public String getUsername() {
        return utilizador.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }
}