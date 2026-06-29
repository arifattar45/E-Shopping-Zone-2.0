package com.cart.service.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.cart.service.demo.exception.GlobalException;

public class GlobalExceptionTest {
	
	@Test
	void testValidationException() {

	    MethodArgumentNotValidException ex =
	            mock(MethodArgumentNotValidException.class);

	    BindingResult result = mock(BindingResult.class);

	    FieldError error = new FieldError("obj", "field", "Invalid");

	    when(ex.getBindingResult()).thenReturn(result);
	    when(result.getFieldErrors()).thenReturn(List.of(error));

	    GlobalException handler = new GlobalException();

	    ResponseEntity<?> res = handler.handleValidation(ex);

	    assertEquals(400, res.getStatusCodeValue());
	}
}
