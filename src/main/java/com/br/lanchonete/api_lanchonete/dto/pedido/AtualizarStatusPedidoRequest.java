package com.br.lanchonete.api_lanchonete.dto.pedido;

import com.br.lanchonete.api_lanchonete.enums.StatusPedido;

import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoRequest(@NotNull StatusPedido status) {
}
