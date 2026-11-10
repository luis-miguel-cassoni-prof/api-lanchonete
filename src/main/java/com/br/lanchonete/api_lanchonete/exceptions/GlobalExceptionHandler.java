package com.br.lanchonete.api_lanchonete.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.br.lanchonete.api_lanchonete.dto.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler (NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(ex.getErrorCode());
        error.setMessage(ex.getMessage());
        error.setStatus(ex.getStatus().value());
        error.setTimestamp(Instant.now());
        error.setPath(request.getRequestURI());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleIncorrectBodyException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    
    ex.getBindingResult().getFieldErrors().forEach(error -> 
        errors.put(error.getField(), error.getDefaultMessage())
    );

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}
}
