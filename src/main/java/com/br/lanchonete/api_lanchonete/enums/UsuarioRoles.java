package com.br.lanchonete.api_lanchonete.enums;

public enum UsuarioRoles {
    AUTENTICADO ("autenticado");

    private String role;

    UsuarioRoles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
