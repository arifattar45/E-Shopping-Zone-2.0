package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CreditRequest;
import com.example.demo.dto.DebitRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.WalletResponse;

public interface WalletService {
	

    WalletResponse createWallet(Long userId);

    WalletResponse getWallet(Long userId);

    WalletResponse credit(CreditRequest request);

    WalletResponse debit(DebitRequest request);
    
    List<TransactionResponse> getTransactions(Long userId);
}