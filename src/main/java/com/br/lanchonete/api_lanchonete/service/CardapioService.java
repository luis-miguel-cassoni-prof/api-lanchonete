package com.br.lanchonete.api_lanchonete.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.lanchonete.api_lanchonete.dto.CardapioRequest;
import com.br.lanchonete.api_lanchonete.dto.CardapioResponse;
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
        log.info("Buscando pelo id: {}!!!", id);
        return cardapioRepository.findById(id).map(lanche -> new CardapioResponse(
            lanche.getId(),
            lanche.getNomeLanche(),
            lanche.getDescricao(),
            lanche.getValor()
        ));
    }

    public void deletarPorId(Short id) {
        log.info("Deletando pelo id: {}", id);
        cardapioRepository.deleteById(id);
    }
}
