package com.br.lanchonete.api_lanchonete.model;

import java.math.BigDecimal;

import com.br.lanchonete.api_lanchonete.enums.StatusPedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 

public class Pedido {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column (nullable = false, length = 50)
    private String nomeCliente;
    
    @ManyToOne
    private Cardapio lanche;

    @Column (nullable = false)
    private Integer quantidade;

    @Column (nullable = false)
    private BigDecimal totalPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status;

    @PrePersist
    private void inicializarStatus() {
        if (status == null) {
            status = StatusPedido.AGUARDANDO;
        }
    }
}
