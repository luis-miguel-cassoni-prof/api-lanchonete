package com.br.lanchonete.api_lanchonete.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
public class Cardapio {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Short id;

    @Column (nullable = false, length = 40)
    private String nomeLanche;

    @Column (nullable = true, length = 200)
    private String descricao;

    @Column (nullable = false)
    private BigDecimal valor;
}
