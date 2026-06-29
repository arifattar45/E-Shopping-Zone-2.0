package com.order.service.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.order.service.demo.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(Long userId);
    
    @Query("SELECT SUM(o.totalAmount) FROM Orders o")
    Double getTotalRevenue();
}