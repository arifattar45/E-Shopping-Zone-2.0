package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.CreateOrderResponse;
import com.example.demo.dto.VerifyPaymentRequest;
import com.example.demo.dto.VerifyPaymentResponse;

import com.example.demo.entity.Wallet;
import com.example.demo.entity.WalletTransaction;

import com.example.demo.enums.TransactionType;

import com.example.demo.exception.WalletNotFoundException;

import com.example.demo.repository.WalletRepository;
import com.example.demo.repository.WalletTransactionRepository;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    @Autowired
    private RazorpayClient razorpayClient;
    
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    @Value("${razorpay.key.id}")
    private String keyId;
    
    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public CreateOrderResponse createOrder(
            Long userId,
            CreateOrderRequest request) throws Exception {

        JSONObject options = new JSONObject();

        // Razorpay expects amount in paise
        options.put("amount", request.getAmount() * 100);

        options.put("currency", "INR");

        options.put("receipt", "wallet_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(options);

        CreateOrderResponse response = new CreateOrderResponse();

        response.setOrderId(order.get("id"));
        response.setAmount(((Number) order.get("amount")).longValue());
        response.setCurrency(order.get("currency"));
        response.setKey(keyId);

        return response;
    }

    @Override
    public VerifyPaymentResponse verifyPayment(
            Long userId,
            VerifyPaymentRequest request) throws Exception {

        JSONObject attributes = new JSONObject();

        attributes.put("razorpay_order_id", request.getRazorpayOrderId());
        attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
        attributes.put("razorpay_signature", request.getRazorpaySignature());

        // Verify Razorpay signature
        Utils.verifyPaymentSignature(attributes, keySecret);

        // Prevent duplicate credit
        if (transactionRepository.findByReferenceId(request.getRazorpayPaymentId()).isPresent()) {
            throw new RuntimeException("Payment already processed");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        BigDecimal rechargeAmount = BigDecimal.valueOf(request.getAmount());

        wallet.setBalance(wallet.getBalance().add(rechargeAmount));

        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();

        transaction.setWallet(wallet);
        transaction.setAmount(rechargeAmount);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setDescription("Wallet Recharge via Razorpay");
        transaction.setReferenceId(request.getRazorpayPaymentId());

        transactionRepository.save(transaction);

        VerifyPaymentResponse response = new VerifyPaymentResponse();

        response.setSuccess(true);
        response.setMessage("Wallet recharged successfully");
        response.setUpdatedBalance(wallet.getBalance());

        return response;
    }
}