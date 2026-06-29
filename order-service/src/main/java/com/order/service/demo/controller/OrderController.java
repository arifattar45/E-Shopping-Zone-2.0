package com.order.service.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.demo.dto.CreateOrderRequest;
import com.order.service.demo.dto.CreateOrderResponse;
import com.order.service.demo.dto.OrderRequest;
import com.order.service.demo.dto.VerifyPaymentRequest;
import com.order.service.demo.dto.VerifyPaymentResponse;
import com.order.service.demo.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

//hello world
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
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.ok(service.checkout(userId, email, request, false));
    }

    // 🔹 USER → Get own orders
    @GetMapping
    @Operation(summary = "Get my orders")
    public ResponseEntity<?> getOrders(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok(service.getMyOrders(userId));
    }

    //ADMIN → Get all orders
    @GetMapping("/all")
    @Operation(summary = "Get all orders (ADMIN)")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get all admin dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(service.getDashboardData());
    }
    
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel Order")
    public ResponseEntity<?> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.cancelOrder(userId, id));
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
    
    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(

            @RequestHeader("X-User-Id") Long userId,

            @RequestBody CreateOrderRequest request)

            throws Exception {

        return ResponseEntity.ok(
                service.createRazorpayOrder(request));

    }
    
    @PostMapping("/verify-payment")
    public ResponseEntity<VerifyPaymentResponse> verifyPayment(

            @RequestHeader("X-User-Id") Long userId,

            @RequestHeader("X-User-Email") String email,

            @RequestBody VerifyPaymentRequest request)

            throws Exception {

        return ResponseEntity.ok(
                service.verifyPayment(userId, email, request));

    }
   
}
