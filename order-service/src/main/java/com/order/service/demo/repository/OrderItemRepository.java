package com.order.service.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.service.demo.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
