package com.itsm.incidentmanagement.security.service;

import com.itsm.incidentmanagement.model.entity.Utilizador;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Utilizador utilizador;

    public CustomUserDetails(Utilizador utilizador) {
        this.utilizador = utilizador;
        System.out.println("🔐 CustomUserDetails criado para: " + utilizador.getUsername());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + utilizador.getRole();
        System.out.println("📋 GrantedAuthority: " + role);
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        String hash = utilizador.getPasswordHash();
        System.out.println("🔑 Password hash: " + hash);
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