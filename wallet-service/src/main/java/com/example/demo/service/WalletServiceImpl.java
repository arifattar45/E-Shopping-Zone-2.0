package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CreditRequest;
import com.example.demo.dto.DebitRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.WalletResponse;
import com.example.demo.entity.Wallet;
import com.example.demo.entity.WalletTransaction;
import com.example.demo.enums.TransactionType;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.WalletAlreadyExistsException;
import com.example.demo.exception.WalletNotFoundException;
import com.example.demo.repository.WalletRepository;
import com.example.demo.repository.WalletTransactionRepository;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletServiceImpl(WalletRepository walletRepository,
                             WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public WalletResponse createWallet(Long userId) {

        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new WalletAlreadyExistsException("Wallet already exists for user : " + userId);
        }

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();

        wallet = walletRepository.save(wallet);

        return mapToResponse(wallet);
    }

    @Override
    public WalletResponse getWallet(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found for user : " + userId));

        return mapToResponse(wallet);
    }

    @Override
    public WalletResponse credit(CreditRequest request) {

        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .amount(request.getAmount())
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .build();

        transactionRepository.save(transaction);

        return mapToResponse(wallet);
    }

    @Override
    public WalletResponse debit(DebitRequest request) {

        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));
        
        System.out.println("==================================");
        System.out.println("Wallet Balance : " + wallet.getBalance());
        System.out.println("Requested Amount : " + request.getAmount());
        System.out.println("Compare Result : " + wallet.getBalance().compareTo(request.getAmount()));
        System.out.println("==================================");

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .amount(request.getAmount())
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .build();

        transactionRepository.save(transaction);

        return mapToResponse(wallet);
    }

    private WalletResponse mapToResponse(Wallet wallet) {

        return WalletResponse.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .build();
    }
    
    @Override
    public List<TransactionResponse> getTransactions(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        return transactionRepository
                .findByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .amount(tx.getAmount())
                        .transactionType(tx.getTransactionType())
                        .description(tx.getDescription())
                        .referenceId(tx.getReferenceId())
                        .createdAt(tx.getCreatedAt())
                        .build())
                .toList();
    }
}