package com.br.lanchonete.api_lanchonete.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.br.lanchonete.api_lanchonete.enums.UsuarioRoles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
public class Usuario implements UserDetails {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Short idUsuario;

    @Column (length = 30, unique = true)
    private String nome;

    @Column (length = 60)
    private String senha;

    @Enumerated (EnumType.STRING)
    private UsuarioRoles role;

    @Override
    public String getUsername() {
        return nome;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRoles.AUTENTICADO) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return List.of();
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
}
