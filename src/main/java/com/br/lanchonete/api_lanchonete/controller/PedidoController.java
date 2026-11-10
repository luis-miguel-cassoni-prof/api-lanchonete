package com.br.lanchonete.api_lanchonete.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.lanchonete.api_lanchonete.dto.pedido.PedidoRequest;
import com.br.lanchonete.api_lanchonete.dto.pedido.PedidoResponse;
import com.br.lanchonete.api_lanchonete.service.PedidoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/pedido")
@Tag (name = "Pedido", description = "Gerenciar os pedidos da api")
public class PedidoController {

    @Autowired 
    PedidoService pedidoService;

    @PostMapping
    public PedidoResponse anotarPedido(@Valid @RequestBody PedidoRequest pedido) {
        return pedidoService.anotarPedido(pedido);
    }

    @GetMapping ("/listarTudo")
    public List<PedidoResponse> listarTudo() {
        return pedidoService.listarTudo();
    }

    @GetMapping(value = "/{id}")
    public Optional<PedidoResponse> buscarPorId(@PathVariable Integer id) {
        return pedidoService.buscarPorId(id);
    }

    @DeleteMapping (value = "/{id}")
    public ResponseEntity<Void> deletarPodId(@PathVariable Integer id) {
        pedidoService.deletarPorId(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping (value = "/{id}")
    public PedidoResponse atualizar(@PathVariable Integer id, @Valid @RequestBody PedidoRequest pedido) {
        return pedidoService.atualizar(id, pedido);
    }
}
