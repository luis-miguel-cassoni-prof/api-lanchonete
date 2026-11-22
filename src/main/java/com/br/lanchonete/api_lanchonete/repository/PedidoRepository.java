package com.br.lanchonete.api_lanchonete.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.br.lanchonete.api_lanchonete.enums.StatusPedido;
import com.br.lanchonete.api_lanchonete.model.Pedido;

import jakarta.persistence.LockModeType;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findAllByOrderByIdAsc();

    Optional<Pedido> findFirstByStatusOrderByIdAsc(StatusPedido status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Pedido> findFirstByStatusNotOrderByIdAsc(StatusPedido status);
}
