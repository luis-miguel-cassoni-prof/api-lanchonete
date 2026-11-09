package com.br.lanchonete.api_lanchonete.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.lanchonete.api_lanchonete.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
}
