package com.user.service.demo;

import com.user.service.demo.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException() {

        RuntimeException ex = new RuntimeException("Error occurred");

        ResponseEntity<?> response = handler.handleRuntime(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}