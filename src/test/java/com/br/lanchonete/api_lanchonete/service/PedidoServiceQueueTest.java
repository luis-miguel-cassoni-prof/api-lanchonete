package com.br.lanchonete.api_lanchonete.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.br.lanchonete.api_lanchonete.enums.StatusPedido;
import com.br.lanchonete.api_lanchonete.model.Pedido;
import com.br.lanchonete.api_lanchonete.repository.CardapioRepository;
import com.br.lanchonete.api_lanchonete.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceQueueTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CardapioRepository cardapioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void advancesOldestOrderToNextStatus() {
        Pedido oldest = pedido(1, "Ana", StatusPedido.AGUARDANDO);
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(oldest));
        when(pedidoRepository.findFirstByStatusNotOrderByIdAsc(StatusPedido.ENTREGUE))
            .thenReturn(Optional.of(oldest));
        when(pedidoRepository.save(oldest)).thenReturn(oldest);

        var result = pedidoService.atualizarStatus(1, StatusPedido.EM_PREPARO);

        assertEquals(StatusPedido.EM_PREPARO, result.status());
        verify(pedidoRepository).save(oldest);
    }

    @Test
    void doesNotAllowNewerOrderToPassQueueHead() {
        Pedido newer = pedido(2, "Bia", StatusPedido.AGUARDANDO);
        Pedido oldest = pedido(1, "Ana", StatusPedido.AGUARDANDO);
        when(pedidoRepository.findById(2)).thenReturn(Optional.of(newer));
        when(pedidoRepository.findFirstByStatusNotOrderByIdAsc(StatusPedido.ENTREGUE))
            .thenReturn(Optional.of(oldest));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> pedidoService.atualizarStatus(2, StatusPedido.EM_PREPARO)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(pedidoRepository, never()).save(newer);
    }

    @Test
    void readyScreenReturnsOnlyOldestReadyCustomerName() {
        Pedido oldestReady = pedido(3, "Caio", StatusPedido.PRONTO);
        when(pedidoRepository.findFirstByStatusOrderByIdAsc(StatusPedido.PRONTO))
            .thenReturn(Optional.of(oldestReady));

        var response = pedidoService.buscarClientePronto();

        assertTrue(response.isPresent());
        assertEquals("Caio", response.orElseThrow().nomeCliente());
    }

    @Test
    void readyScreenHasNoCustomerWhenNoOrderIsReady() {
        when(pedidoRepository.findFirstByStatusOrderByIdAsc(StatusPedido.PRONTO))
            .thenReturn(Optional.empty());

        assertFalse(pedidoService.buscarClientePronto().isPresent());
    }

    private Pedido pedido(Integer id, String nome, StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setNomeCliente(nome);
        pedido.setStatus(status);
        return pedido;
    }
}
