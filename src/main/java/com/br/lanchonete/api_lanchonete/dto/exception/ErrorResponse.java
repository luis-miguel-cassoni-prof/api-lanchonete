package com.br.lanchonete.api_lanchonete.dto.exception;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public  class  ErrorResponse { 
    private String errorCode; 
    private String message; 
    private int status; 
    private Instant timestamp; 
    private String path; 
}
