package com.example.demo.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CreditRequest;
import com.example.demo.dto.DebitRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.WalletResponse;
import com.example.demo.service.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallet")
@Validated
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<WalletResponse> createWallet(@PathVariable Long userId) {
        return new ResponseEntity<>(walletService.createWallet(userId), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getWallet(
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(walletService.getWallet(userId));
    }
    
    
    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> credit(@Valid @RequestBody CreditRequest request) {
        return ResponseEntity.ok(walletService.credit(request));
    }

    @PostMapping("/debit")
    public ResponseEntity<WalletResponse> debit(@Valid @RequestBody DebitRequest request) {
        return ResponseEntity.ok(walletService.debit(request));
    }
    
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> transactions(
            @RequestHeader("X-User-Id") Long userId){

        return ResponseEntity.ok(
                walletService.getTransactions(userId));
    }
}