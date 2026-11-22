package com.br.lanchonete.api_lanchonete.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.lanchonete.api_lanchonete.dto.usuario.CsrfTokenResponse;
import com.br.lanchonete.api_lanchonete.dto.usuario.LoginRequest;
import com.br.lanchonete.api_lanchonete.dto.usuario.LoginResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuário", description = "Autenticação e sessão do usuário")
public class UsuarioController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

    public UsuarioController(
        AuthenticationManager authenticationManager,
        SecurityContextRepository securityContextRepository,
        SessionAuthenticationStrategy sessionAuthenticationStrategy
    ) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    @GetMapping("/csrf")
    public CsrfTokenResponse csrfToken(CsrfToken csrfToken) {
        return new CsrfTokenResponse(csrfToken.getHeaderName(), csrfToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.nome(), loginRequest.senha())
            );
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
        }

        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.ok(new LoginResponse(authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}
