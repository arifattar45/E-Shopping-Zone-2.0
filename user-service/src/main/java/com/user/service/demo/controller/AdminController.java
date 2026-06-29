package com.user.service.demo.controller;

import com.user.service.demo.entity.User;
import com.user.service.demo.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs", description = "Admin operations to manage users")
public class AdminController {

    private final UserRepository repo;

    public AdminController(UserRepository repo) {
        this.repo = repo;
    }

    //Get all users
    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    //Delete user
    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        repo.deleteById(id);
        return "User deleted successfully";
    }

    //Promote user to admin
    @Operation(summary = "Promote user to ADMIN")
    @PutMapping("/promote/{id}")
    public String promoteUser(@PathVariable Long id) {
        User user = repo.findById(id).orElseThrow();
        user.setRole("ROLE_ADMIN");
        repo.save(user);
        return "User promoted to ADMIN";
    }
}
