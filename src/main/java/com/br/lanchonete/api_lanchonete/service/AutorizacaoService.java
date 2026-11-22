package com.br.lanchonete.api_lanchonete.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.br.lanchonete.api_lanchonete.repository.UsuarioRepository;

@Service 
public class AutorizacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutorizacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByNome(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

}
