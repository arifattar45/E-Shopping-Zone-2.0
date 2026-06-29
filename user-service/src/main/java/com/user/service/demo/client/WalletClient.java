package com.user.service.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.user.service.demo.dto.WalletResponse;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/wallet/create/{userId}")
    WalletResponse createWallet(@PathVariable Long userId);

}