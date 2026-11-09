package com.br.lanchonete.api_lanchonete.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 

public class Pedido {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column (nullable = false, length = 50)
    String nomeCliente;
    
    @ManyToOne
    Cardapio lanche;

    @Column (nullable = false)
    Integer quantidade;

    @Column (nullable = false)
    BigDecimal totalPedido;
}
