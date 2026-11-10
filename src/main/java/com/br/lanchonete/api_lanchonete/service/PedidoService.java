package com.br.lanchonete.api_lanchonete.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.br.lanchonete.api_lanchonete.dto.pedido.PedidoRequest;
import com.br.lanchonete.api_lanchonete.dto.pedido.PedidoResponse;
import com.br.lanchonete.api_lanchonete.exceptions.NotFoundException;
import com.br.lanchonete.api_lanchonete.model.Cardapio;
import com.br.lanchonete.api_lanchonete.model.Pedido;
import com.br.lanchonete.api_lanchonete.repository.CardapioRepository;
import com.br.lanchonete.api_lanchonete.repository.PedidoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class PedidoService {
    
    @Autowired 
    PedidoRepository pedidoRepository;

    @Autowired 
    CardapioRepository cardapioRepository;

    public PedidoResponse anotarPedido(PedidoRequest pedidoRequest) {
        Pedido pedido = new Pedido();
        Cardapio cardapio = cardapioRepository.findById(pedidoRequest.lancheId()).orElseThrow(() ->
        new NotFoundException("Lanche não encontrado!", "CARDAPIO_NOT_FOUND", HttpStatus.NOT_FOUND));

        BigDecimal total = cardapio.getValor().multiply(BigDecimal.valueOf(pedidoRequest.quantidade()));

        pedido.setNomeCliente(pedidoRequest.nomeCliente());
        pedido.setLanche(cardapio);
        pedido.setQuantidade(pedidoRequest.quantidade());
        pedido.setTotalPedido(total);

        log.info("Anotando um pedido!!!");
        pedidoRepository.save(pedido);

        return new PedidoResponse(pedido.getId(), pedido.getNomeCliente(), pedido.getLanche(), 
            pedido.getQuantidade(), pedido.getTotalPedido());
    }

    public List<PedidoResponse> listarTudo() {
        List<Pedido> listaPedidos = pedidoRepository.findAll();
        List<PedidoResponse> response = new ArrayList<>();

        for(Pedido pedido : listaPedidos) {
            response.add(new PedidoResponse(pedido.getId(), pedido.getNomeCliente(), pedido.getLanche(), 
                pedido.getQuantidade(), pedido.getTotalPedido()));
        }

        log.info("Listando todos os pedidos!!!");
        return response;
    }

    public Optional<PedidoResponse> buscarPorId(Integer id) {
        log.info("Buscando pedido pelo id: {}!!!", id);

        if(!pedidoRepository.existsById(id)) {
            throw new NotFoundException("Pedido não encontrado", "PEDIDO_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        return pedidoRepository.findById(id).map(pedido -> new PedidoResponse(
            pedido.getId(),
            pedido.getNomeCliente(),
            pedido.getLanche(),
            pedido.getQuantidade(),
            pedido.getTotalPedido()
        ));
    }

    public void deletarPorId(Integer id) {
        log.info("Deletando pedido pelo ID: {}", id);

        if(!pedidoRepository.existsById(id)) {
            throw new NotFoundException("Pedido não encontrado", "PEDIDO_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        pedidoRepository.deleteById(id);
    }

    public PedidoResponse atualizar(Integer id, PedidoRequest pedidoRequest) {
        Pedido pedido = new Pedido();
        Cardapio cardapio = cardapioRepository.findById(pedidoRequest.lancheId()).orElseThrow(() ->
        new NotFoundException("Lanche não encontrado!", "CARDAPIO_NOT_FOUND", HttpStatus.NOT_FOUND));

        BigDecimal total = cardapio.getValor().multiply(BigDecimal.valueOf(pedidoRequest.quantidade()));

        pedido.setNomeCliente(pedidoRequest.nomeCliente());
        pedido.setLanche(cardapio);
        pedido.setQuantidade(pedidoRequest.quantidade());
        pedido.setTotalPedido(total);

        log.info("Atualizando pedido pelo ID: {}", id);

        pedidoRepository.save(pedido);

        return new PedidoResponse(pedido.getId(), pedido.getNomeCliente(), pedido.getLanche(), 
            pedido.getQuantidade(), pedido.getTotalPedido());
    }
}
