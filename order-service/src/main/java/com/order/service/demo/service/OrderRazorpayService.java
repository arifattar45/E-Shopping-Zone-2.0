package com.order.service.demo.service;

import com.order.service.demo.dto.CreateOrderRequest;
import com.order.service.demo.dto.CreateOrderResponse;
import com.order.service.demo.dto.VerifyPaymentRequest;
import com.order.service.demo.dto.VerifyPaymentResponse;

public interface OrderRazorpayService {

    CreateOrderResponse createOrder(
            Long userId,
            CreateOrderRequest request) throws Exception;

    VerifyPaymentResponse verifyPayment(
            Long userId,
            String email,
            VerifyPaymentRequest request) throws Exception;
}