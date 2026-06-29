package com.order.service.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.order.service.demo.exception.GlobalException;

class GlobalExceptionTest {

    private GlobalException handler = new GlobalException();

    @Test
    void testRuntimeException() {
        ResponseEntity<?> res = handler.handleRuntime(new RuntimeException("Error"));
        assertEquals(400, res.getStatusCodeValue());
    }
    
    @Test
    void testValidationException() {

        GlobalException handler = new GlobalException();

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));

        ResponseEntity<?> res = handler.handleValidation(ex);

        assertEquals(400, res.getStatusCodeValue());
    }
   
}
