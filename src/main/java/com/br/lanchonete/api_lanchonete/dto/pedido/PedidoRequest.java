package com.br.lanchonete.api_lanchonete.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoRequest(
    @NotBlank String nomeCliente, 
    @NotNull @Positive Short lancheId, 
    @NotNull @Positive Integer quantidade) {
}
