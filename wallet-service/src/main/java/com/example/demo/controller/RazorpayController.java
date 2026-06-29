package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.CreateOrderResponse;
import com.example.demo.dto.VerifyPaymentRequest;
import com.example.demo.dto.VerifyPaymentResponse;
import com.example.demo.service.RazorpayService;

@RestController
@RequestMapping("/wallet/recharge")
public class RazorpayController {

    private final RazorpayService razorpayService;

    public RazorpayController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request) throws Exception {

        return ResponseEntity.ok(
                razorpayService.createOrder(userId, request));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<VerifyPaymentResponse> verifyPayment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody VerifyPaymentRequest request) throws Exception {

        return ResponseEntity.ok(
                razorpayService.verifyPayment(userId, request));
    }
}