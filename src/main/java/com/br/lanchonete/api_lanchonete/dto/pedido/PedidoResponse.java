package com.br.lanchonete.api_lanchonete.dto.pedido;

import java.math.BigDecimal;

import com.br.lanchonete.api_lanchonete.model.Cardapio;

public record PedidoResponse (Integer id, String nomeCliente, Cardapio lanche, Integer quantidade, BigDecimal totalPedido) {

}
