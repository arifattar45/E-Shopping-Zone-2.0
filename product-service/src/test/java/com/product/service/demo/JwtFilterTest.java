package com.product.service.demo;


import com.product.service.demo.filter.JwtFilter;
import com.product.service.demo.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.mockito.*;

import jakarta.servlet.FilterChain;

import org.springframework.mock.web.*;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtFilter filter;

    public JwtFilterTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSkipGetProducts() throws Exception {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setServletPath("/products");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    @Test
    void testWithToken() throws Exception {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setServletPath("/products");
        req.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtil.extractRole("token")).thenReturn("ROLE_ADMIN");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }
}
