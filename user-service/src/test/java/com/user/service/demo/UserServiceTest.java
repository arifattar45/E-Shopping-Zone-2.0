package com.user.service.demo;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.service.demo.dto.LoginRequest;
import com.user.service.demo.dto.LoginResponse;
import com.user.service.demo.dto.RegisterRequest;
import com.user.service.demo.entity.User;
import com.user.service.demo.repository.UserRepository;
import com.user.service.demo.service.UserService;
import com.user.service.demo.util.JwtUtil;

class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService service;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ REGISTER SUCCESS
    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.name = "Arif";
        req.email = "arif@gmail.com";
        req.password = "123456";

        when(repo.findByEmail(req.email)).thenReturn(null);
        when(encoder.encode(req.password)).thenReturn("encoded");

        String result = service.register(req);

        assertEquals("User Registered", result);
        verify(repo).save(any(User.class));
    }

    // ❌ REGISTER FAIL (already exists)
    @Test
    void testRegisterFail() {
        RegisterRequest req = new RegisterRequest();
        req.email = "arif@gmail.com";

        when(repo.findByEmail(req.email)).thenReturn(new User());

        assertThrows(RuntimeException.class, () -> service.register(req));
    }

    // ✅ LOGIN SUCCESS
    @Test
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest();
        req.setEmail("arif@gmail.com");
        req.setPassword("123");

        User user = new User();
        user.setEmail("arif@gmail.com");
        user.setPassword("encoded");
        user.setRole("ROLE_USER");

        when(repo.findByEmail(req.getEmail())).thenReturn(user);
        when(encoder.matches("123", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("token123");

        LoginResponse res = service.login(req);

        assertEquals("Welcome User", res.getMessage());
        assertEquals("token123", res.getToken());
    }

    // ❌ LOGIN FAIL
    @Test
    void testLoginFail() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@gmail.com");
        req.setPassword("wrong");

        when(repo.findByEmail(req.getEmail())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.login(req));
    }

    // ✅ FORGOT PASSWORD
    @Test
    void testForgot() {
        User user = new User();
        user.setEmail("arif@gmail.com");

        when(repo.findByEmail("arif@gmail.com")).thenReturn(user);

        String token = service.forgot("arif@gmail.com");

        assertNotNull(token);
        verify(repo).save(user);
    }

    // ✅ RESET SUCCESS
    @Test
    void testResetSuccess() {
        User user = new User();
        user.setResetToken("token");
        user.setTokenExpiry(System.currentTimeMillis() + 10000);

        when(repo.findByResetToken("token")).thenReturn(user);
        when(encoder.encode("newpass")).thenReturn("encoded");

        String res = service.reset("token", "newpass");

        assertEquals("Password Updated", res);
        verify(repo).save(user);
    }

    // ❌ RESET FAIL
    @Test
    void testResetFail() {
        when(repo.findByResetToken("bad")).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> service.reset("bad", "123"));
    }
}