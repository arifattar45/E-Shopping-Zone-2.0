package com.user.service.demo;

import com.user.service.demo.security.JwtFilter;
import com.user.service.demo.util.JwtUtil;
import com.user.service.demo.repository.UserRepository;
import com.user.service.demo.entity.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.FilterChain;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository repo;

    @InjectMocks
    private JwtFilter filter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST: skip /auth endpoints
    @Test
    void testSkipAuthEndpoints() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    // ✅ TEST: valid token flow
    @Test
    void testValidToken() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/admin/users");
        request.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        User user = new User();
        user.setEmail("arif@gmail.com");
        user.setRole("ROLE_USER");

        when(jwtUtil.extractEmail("token")).thenReturn("arif@gmail.com");
        when(jwtUtil.isTokenValid("token")).thenReturn(true);
        when(repo.findByEmail("arif@gmail.com")).thenReturn(user);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }
}