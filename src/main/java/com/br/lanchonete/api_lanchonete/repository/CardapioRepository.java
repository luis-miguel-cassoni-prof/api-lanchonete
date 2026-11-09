package com.br.lanchonete.api_lanchonete.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.lanchonete.api_lanchonete.model.Cardapio;

public interface CardapioRepository extends JpaRepository<Cardapio, Short> {
    
    List<Cardapio> findByNomeLanche(String nomeLanche);
    
    List<Cardapio> findByValorLessThan(BigDecimal valor);

    List<Cardapio> findByValorGreaterThan(BigDecimal valor);
}
