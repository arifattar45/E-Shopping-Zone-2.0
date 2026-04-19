package com.order.service.demo.fiegn;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.order.service.demo.dto.CartItem;

@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/cart")
    List<CartItem> getCart(@RequestHeader("Authorization") String token);

    @DeleteMapping("/cart/clear")
    void clearCart(@RequestHeader("Authorization") String token);
}