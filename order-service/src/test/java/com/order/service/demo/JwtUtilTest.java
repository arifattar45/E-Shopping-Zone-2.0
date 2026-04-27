package com.order.service.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.order.service.demo.util.JwtUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtilTest {
	
	@Test
	void testExtractUserId() {

	    JwtUtil jwt = new JwtUtil();

	    String token = Jwts.builder()
	            .claim("userId", 1L)
	            .signWith(Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey12".getBytes()))
	            .compact();

	    Long id = jwt.extractUserId(token);

	    assertEquals(1L, id);
	}
}
