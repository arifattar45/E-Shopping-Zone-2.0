package com.example.demo.dto;


import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {

    private Long walletId;
    private Long userId;
    private BigDecimal balance;
}
