package com.cart.service.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.cart.service.demo.util.JwtUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

    private JwtUtil jwtUtil = new JwtUtil();

    private String generateToken() {
        return Jwts.builder()
                .claim("userId", 1L)
                .claim("role", "USER")
                .signWith(Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey12".getBytes()))
                .compact();
    }

    @Test
    void testExtractUserId() {
        String token = generateToken();
        Long userId = jwtUtil.extractUserId(token);
        assertEquals(1L, userId);
    }

    @Test
    void testExtractRole() {
        String token = generateToken();
        String role = jwtUtil.extractRole(token);
        assertEquals("USER", role);
    }
}
