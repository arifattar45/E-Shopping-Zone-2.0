package com.user.service.demo;

import com.user.service.demo.entity.User;
import com.user.service.demo.util.JwtUtil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil = new JwtUtil();

    @Test
    void testGenerateAndValidateToken() {

        User user = new User();
        user.setId(1L);
        user.setEmail("arif@gmail.com");
        user.setRole("ROLE_USER");

        String token = jwtUtil.generateToken(user);

        // ✅ checks
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals("arif@gmail.com", jwtUtil.extractEmail(token));
        assertEquals(1L, jwtUtil.extractUserId(token));
        assertEquals("ROLE_USER", jwtUtil.extractRole(token));
    }
}