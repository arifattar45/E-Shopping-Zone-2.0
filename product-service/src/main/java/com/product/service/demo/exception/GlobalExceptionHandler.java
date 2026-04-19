package com.product.service.demo.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound(ProductNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }
    
    @ExceptionHandler(PageNotFoundException.class)
    public ResponseEntity<?> handlePageNotFound(PageNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
//    @ExceptionHandler(ProductNotFoundException.class)
//    public ResponseEntity<?> handleProductNotFound1(ProductNotFoundException ex) {
//        return ResponseEntity.status(404).body(ex.getMessage());
//    }
}