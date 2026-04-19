package com.order.service.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.demo.dto.CartItem;
import com.order.service.demo.dto.OrderRequest;
import com.order.service.demo.entity.OrderItem;
import com.order.service.demo.entity.Orders;
import com.order.service.demo.fiegn.CartClient;
import com.order.service.demo.repository.OrderItemRepository;
import com.order.service.demo.repository.OrderRepository;
import com.order.service.demo.util.JwtUtil;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final CartClient cartClient;
    private final JwtUtil jwtUtil;

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository itemRepo,
                        CartClient cartClient,
                        JwtUtil jwtUtil) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.cartClient = cartClient;
        this.jwtUtil = jwtUtil;
    }

    public Orders checkout(String token, OrderRequest request) {

        Long userId = jwtUtil.extractUserId(token);

        List<CartItem> cartItems = cartClient.getCart("Bearer " + token);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        if (!request.getPaymentMethod().equalsIgnoreCase("COD")
                && !request.getPaymentMethod().equalsIgnoreCase("UPI")) {
            throw new RuntimeException("Invalid payment method");
        }

        double total = cartItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        Orders order = new Orders();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus("SUCCESS");
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());

        Orders saved = orderRepo.save(order);

        for (CartItem c : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(saved.getId());
            item.setProductId(c.getProductId());
            item.setQuantity(c.getQuantity());
            item.setPrice(c.getPrice());
            item.setProductName(c.getProductName());
            itemRepo.save(item);
        }

        cartClient.clearCart("Bearer " + token);

        return saved;
    }

    public List<Orders> getMyOrders(String token) {
        Long userId = jwtUtil.extractUserId(token);
        return orderRepo.findByUserId(userId);
    }
    
    public List<Orders> getAllOrders() {
        return orderRepo.findAll();
    }
    
    
    public Orders updateStatus(Long id, String status) {

        Orders order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        //basic validation
        if (order.getStatus().equals("DELIVERED")) {
            throw new RuntimeException("Cannot modify delivered order");
        }

        order.setStatus(status);

        //Refund logic (mock)
        if (status.equalsIgnoreCase("REFUNDED")) {
            order.setPaymentStatus("REFUNDED");
        }

        return orderRepo.save(order);
    }
}