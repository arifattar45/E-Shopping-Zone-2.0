package com.order.service.demo;


import com.order.service.demo.filter.JwtFilter;
import com.order.service.demo.util.JwtUtil;

import org.junit.jupiter.api.*;
import org.mockito.*;

import jakarta.servlet.FilterChain;

import org.springframework.mock.web.*;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtFilter filter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Skip GET /products
    @Test
    void testSkipPublicEndpoint() throws Exception {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setServletPath("/products");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    // ✅ No token case
    @Test
    void testNoToken() throws Exception {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setServletPath("/orders");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    // ✅ Valid token
    @Test
    void testValidToken() throws Exception {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setServletPath("/orders");
        req.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtil.extractRole("token")).thenReturn("ROLE_USER");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }

    // ✅ Invalid token (exception branch)
    @Test
    void testInvalidToken() throws Exception {

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setServletPath("/orders");
        req.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtil.extractRole("token")).thenThrow(new RuntimeException());

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
    }
}