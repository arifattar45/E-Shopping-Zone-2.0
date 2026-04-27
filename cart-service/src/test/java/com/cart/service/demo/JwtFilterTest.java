package com.cart.service.demo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.cart.service.demo.filter.JwtFilter;
import com.cart.service.demo.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtFilterTest {

    private JwtUtil jwtUtil = mock(JwtUtil.class);
    private JwtFilter filter = new JwtFilter(jwtUtil);

    @Test
    void testDoFilter() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token");

        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }
}