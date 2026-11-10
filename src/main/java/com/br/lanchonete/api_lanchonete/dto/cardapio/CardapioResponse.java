package com.br.lanchonete.api_lanchonete.dto.cardapio;

import java.math.BigDecimal;

public record CardapioResponse(Short id, String nomeLanche, String descricao, BigDecimal valor) {

}
