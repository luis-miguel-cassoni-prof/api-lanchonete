package com.br.lanchonete.api_lanchonete.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import com.br.lanchonete.api_lanchonete.dto.usuario.LoginRequest;
import com.br.lanchonete.api_lanchonete.dto.usuario.LoginResponse;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @InjectMocks
    private UsuarioController controller;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginSavesAuthenticatedContextInSessionRepository() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        var result = controller.login(new LoginRequest("admin", "secret"), request, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(new LoginResponse("admin"), result.getBody());
        verify(sessionAuthenticationStrategy).onAuthentication(authentication, request, response);

        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(securityContextRepository).saveContext(contextCaptor.capture(), eq(request), eq(response));
        assertSame(authentication, contextCaptor.getValue().getAuthentication());
    }

    @Test
    void loginReturnsUnauthorizedForInvalidCredentials() {
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        var result = controller.login(
            new LoginRequest("admin", "wrong"),
            new MockHttpServletRequest(),
            new MockHttpServletResponse()
        );

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), assertInstanceOf(ProblemDetail.class, result.getBody()).getStatus());
    }

    @Test
    void logoutInvalidatesSessionAndClearsAuthentication() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        var session = request.getSession();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(mock(Authentication.class));
        SecurityContextHolder.setContext(context);

        var result = controller.logout(request);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertThrows(IllegalStateException.class, () -> session.getAttribute("test"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
