package com.br.lanchonete.api_lanchonete.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.br.lanchonete.api_lanchonete.dto.cardapio.CardapioRequest;
import com.br.lanchonete.api_lanchonete.dto.cardapio.CardapioResponse;
import com.br.lanchonete.api_lanchonete.exceptions.NotFoundException;
import com.br.lanchonete.api_lanchonete.model.Cardapio;
import com.br.lanchonete.api_lanchonete.repository.CardapioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class CardapioService {

    @Autowired 
    CardapioRepository cardapioRepository;

    public CardapioResponse cadastrarLanche(CardapioRequest lanche) {
        Cardapio cardapio = new Cardapio();

        cardapio.setNomeLanche(lanche.nomeLanche());
        cardapio.setDescricao(lanche.descricao());
        cardapio.setValor(lanche.valor());

        cardapioRepository.save(cardapio);

        log.info("Cadastrando lanche!!!");

        return new CardapioResponse(cardapio.getId(), cardapio.getNomeLanche(), cardapio.getDescricao(), cardapio.getValor());
    }

    public List<CardapioResponse> listarTudo() {
        List<Cardapio> cardapio = cardapioRepository.findAll();
        List<CardapioResponse> resposta = new ArrayList<>();

        for (Cardapio lanche : cardapio) {
            resposta.add(new CardapioResponse(lanche.getId(), lanche.getNomeLanche(), lanche.getDescricao(), lanche.getValor()));
        }

        log.info("Listando todos os lanches!!!");

        return resposta;
    }

    public Optional<CardapioResponse> buscarPorId(Short id) {
        log.info("Buscando lanche pelo id: {}!!!", id);

        if(!cardapioRepository.existsById(id)) {
            throw new NotFoundException("Lanche não encontrado!", "CARDAPIO_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        return cardapioRepository.findById(id).map(lanche -> new CardapioResponse(
            lanche.getId(),
            lanche.getNomeLanche(),
            lanche.getDescricao(),
            lanche.getValor()
        ));
    }

    public void deletarPorId(Short id) {
        log.info("Deletando lanche pelo id: {}", id);
        if (!cardapioRepository.existsById(id)) {
            throw new NotFoundException("Lanche não encontrado!", "CARDAPIO_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        cardapioRepository.deleteById(id);
    }

    public CardapioResponse atualizar(Short id, CardapioRequest lanche) {
        Cardapio cardapio = cardapioRepository.findById(id).orElseThrow(() -> new NotFoundException("Lanche não encontrado!", "CARDAPIO_NOT_FOUND", HttpStatus.NOT_FOUND));

        cardapio.setNomeLanche(lanche.nomeLanche());
        cardapio.setDescricao(lanche.descricao());
        cardapio.setValor(lanche.valor());

        log.info("Atualizando lanche do ID: {}!!!", id);

        cardapioRepository.save(cardapio);


        return new CardapioResponse(cardapio.getId(), cardapio.getNomeLanche(), cardapio.getDescricao(), cardapio.getValor());
    }
}