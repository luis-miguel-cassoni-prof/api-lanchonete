package com.br.lanchonete.api_lanchonete.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.lanchonete.api_lanchonete.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Short> {
    Optional<Usuario> findByNome(String nome);
}
