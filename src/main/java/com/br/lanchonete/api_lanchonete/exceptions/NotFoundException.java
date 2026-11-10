package com.br.lanchonete.api_lanchonete.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode (callSuper = true)
@NoArgsConstructor (force = true)
@Data 
public class NotFoundException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    public NotFoundException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
