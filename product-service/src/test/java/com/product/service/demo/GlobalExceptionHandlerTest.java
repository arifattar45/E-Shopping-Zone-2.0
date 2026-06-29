package com.product.service.demo;

import com.product.service.demo.exception.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testProductNotFound() {
        ResponseEntity<?> res = handler.handleProductNotFound(
                new ProductNotFoundException("Not found"));

        assertEquals(404, res.getStatusCodeValue());
    }

    @Test
    void testPageNotFound() {
        ResponseEntity<?> res = handler.handlePageNotFound(
                new PageNotFoundException("Page error"));

        assertEquals(400, res.getStatusCodeValue());
    }
}