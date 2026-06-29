package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.CreateOrderResponse;
import com.example.demo.dto.VerifyPaymentRequest;
import com.example.demo.dto.VerifyPaymentResponse;

public interface RazorpayService {

	    CreateOrderResponse createOrder(Long userId,
	                                    CreateOrderRequest request) throws Exception;

	    VerifyPaymentResponse verifyPayment(Long userId,
	                                       VerifyPaymentRequest request) throws Exception;
	}