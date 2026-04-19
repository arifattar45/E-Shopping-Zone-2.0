package com.order.service.demo.controller;

import com.order.service.demo.dto.OrderRequest;
import com.order.service.demo.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order APIs")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // 🔹 USER → Checkout
    @PostMapping("/checkout")
    @Operation(summary = "Checkout and place order")
    public ResponseEntity<?> checkout(
            @RequestHeader("Authorization") String header,
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.ok(service.checkout(header.substring(7), request));
    }

    // 🔹 USER → Get own orders
    @GetMapping
    @Operation(summary = "Get my orders")
    public ResponseEntity<?> getOrders(
            @RequestHeader("Authorization") String header
    ) {
        return ResponseEntity.ok(service.getMyOrders(header.substring(7)));
    }

    //ADMIN → Get all orders
    @GetMapping("/all")
    @Operation(summary = "Get all orders (ADMIN)")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    //ADMIN → Update order status
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status (ADMIN)")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String value
    ) {
        return ResponseEntity.ok(service.updateStatus(id, value));
    }
}