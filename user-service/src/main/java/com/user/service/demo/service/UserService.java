package com.user.service.demo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.service.demo.dto.LoginRequest;
import com.user.service.demo.dto.LoginResponse;
import com.user.service.demo.dto.RegisterRequest;
import com.user.service.demo.entity.User;
import com.user.service.demo.repository.UserRepository;
import com.user.service.demo.util.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest req) {
    	
    	if (repo.findByEmail(req.email) != null) {
    	    throw new RuntimeException("User already registered");
    	}

        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));
        user.setRole("ROLE_USER");

        repo.save(user);
        return "User Registered";
    }

    public LoginResponse login(LoginRequest req) {

        User user = repo.findByEmail(req.getEmail());

        if (user != null && encoder.matches(req.getPassword(), user.getPassword())) {

            // ✅ pass full user (for userId + role in JWT)
            String token = jwtUtil.generateToken(user);

            // ✅ role-based message
            String message = user.getRole().equals("ROLE_ADMIN")
                    ? "Welcome Admin, full access"
                    : "Welcome User";

            return new LoginResponse(message, token);
        }

        throw new RuntimeException("Invalid credentials");
    }

    public String forgot(String email) {

        User user = repo.findByEmail(email);

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(System.currentTimeMillis() + 300000);

        repo.save(user);
        return token;
    }

    public String reset(String token, String newPassword) {

        User user = repo.findByResetToken(token);

        if (user == null || user.getTokenExpiry() < System.currentTimeMillis()) {
            throw new RuntimeException("Invalid token");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);

        repo.save(user);
        return "Password Updated";
    }
}
