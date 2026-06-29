package com.order.service.demo.fiegn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.order.service.demo.dto.WalletDebitRequest;
import com.order.service.demo.dto.WalletResponse;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/wallet/debit")
    WalletResponse debit(@RequestBody WalletDebitRequest request);

    @GetMapping("/wallet/{userId}")
    WalletResponse getWallet(@PathVariable Long userId);

}