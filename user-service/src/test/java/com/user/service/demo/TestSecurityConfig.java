package com.user.service.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.user.service.demo.util.JwtUtil;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();   // dummy bean
    }
}