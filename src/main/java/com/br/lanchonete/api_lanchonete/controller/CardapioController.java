package com.br.lanchonete.api_lanchonete.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.lanchonete.api_lanchonete.dto.cardapio.CardapioRequest;
import com.br.lanchonete.api_lanchonete.dto.cardapio.CardapioResponse;
import com.br.lanchonete.api_lanchonete.service.CardapioService;

@RestController 
@RequestMapping ("/cardapio")
public class CardapioController {
    
    @Autowired 
    CardapioService cardapioService;

    @PostMapping
    public CardapioResponse cadastrarLanche(@RequestBody CardapioRequest lanche) {
        return cardapioService.cadastrarLanche(lanche);
    }

    @GetMapping ("/listarTudo")
    public List<CardapioResponse> listarTudo() {
        return cardapioService.listarTudo();
    }

    @GetMapping (value = "/{id}")
    public Optional<CardapioResponse> buscarPorId(@PathVariable Short id) {
        return cardapioService.buscarPorId(id);
    }

    @DeleteMapping (value = "/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Short id) {
        cardapioService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping (value = "/{id}")
    public CardapioResponse atualizar(@PathVariable Short id, @RequestBody CardapioRequest lanche) {
        return cardapioService.atualizar(id, lanche);
    }
}
