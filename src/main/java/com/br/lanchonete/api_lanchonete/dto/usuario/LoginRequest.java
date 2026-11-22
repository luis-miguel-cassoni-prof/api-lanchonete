package com.br.lanchonete.api_lanchonete.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String nome,
    @NotBlank String senha
) {
}
