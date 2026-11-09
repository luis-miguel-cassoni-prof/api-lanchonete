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
    Short id;

    @Column (nullable = false, length = 40)
    String nomeLanche;

    @Column (nullable = true, length = 200)
    String descricao;

    @Column (nullable = false)
    BigDecimal valor;
}
