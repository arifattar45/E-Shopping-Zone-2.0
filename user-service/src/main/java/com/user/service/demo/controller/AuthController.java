package com.user.service.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.user.service.demo.dto.LoginRequest;
import com.user.service.demo.dto.RegisterRequest;
import com.user.service.demo.dto.ResetRequest;
import com.user.service.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication APIs", description = "Handles user registration, login, and password management")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @Operation(
        summary = "Register User",
        description = "Registers a new user with name, email, and password"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        String message = service.register(req);
        return ResponseEntity.ok().body(message);
    }

    @Operation(
        summary = "Login User",
        description = "Authenticates user and returns JWT token"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }

    @Operation(
        summary = "Forgot Password",
        description = "Generates a reset token for the given email"
    )
    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestParam String email) {
        String token = service.forgot(email);
        return ResponseEntity.ok().body("Reset token: " + token);
    }

    @Operation(
        summary = "Reset Password",
        description = "Resets user password using token"
    )
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@Valid @RequestBody ResetRequest req) {
        String message = service.reset(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok().body(message);
    }
}