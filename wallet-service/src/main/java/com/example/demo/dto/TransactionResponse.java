package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.enums.TransactionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {

    private Long id;

    private BigDecimal amount;

    private TransactionType transactionType;

    private String description;

    private String referenceId;

    private LocalDateTime createdAt;
}