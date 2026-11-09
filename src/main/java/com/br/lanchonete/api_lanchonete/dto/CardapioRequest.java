package com.br.lanchonete.api_lanchonete.dto;

import java.math.BigDecimal;

public record CardapioRequest(String nomeLanche, String descricao, BigDecimal valor) {

}
