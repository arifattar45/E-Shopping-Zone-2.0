package com.cart.service.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cart.service.demo.entity.CartItem;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
