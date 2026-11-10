package com.br.lanchonete.api_lanchonete.dto.cardapio;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CardapioRequest(
    @NotBlank String nomeLanche,
    @NotBlank String descricao, 
    @NotNull BigDecimal valor) {

}
