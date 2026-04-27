package com.order.service.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.order.service.demo.dto.CartItem;
import com.order.service.demo.dto.OrderRequest;
import com.order.service.demo.entity.OrderItem;
import com.order.service.demo.entity.Orders;
import com.order.service.demo.fiegn.CartClient;
import com.order.service.demo.repository.OrderItemRepository;
import com.order.service.demo.repository.OrderRepository;
import com.order.service.demo.service.OrderService;
import com.order.service.demo.util.JwtUtil;
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private OrderItemRepository itemRepo;

    @Mock
    private CartClient cartClient;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OrderService service;
    
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInvalidPayment() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        CartItem item = new CartItem();
        item.setProductId(1L);
        item.setProductName("Phone");
        item.setQuantity(2);
        item.setPrice(100.0);

        when(cartClient.getCart(any())).thenReturn(List.of(item));

        OrderRequest req = new OrderRequest();
        req.setPaymentMethod("INVALID");

        assertThrows(RuntimeException.class,
                () -> service.checkout("token", req));
    }

    @Test
    void testCheckoutEmptyCart() {
        when(jwtUtil.extractUserId("token")).thenReturn(1L);
        when(cartClient.getCart(any())).thenReturn(new ArrayList<>());

        OrderRequest req = new OrderRequest();
        req.setPaymentMethod("COD");

        assertThrows(RuntimeException.class,
                () -> service.checkout("token", req));
    }

    @Test
    void testGetMyOrders() {
        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        Orders order = new Orders();
        order.setId(1L);

        when(orderRepo.findByUserId(1L)).thenReturn(List.of(order));
        when(itemRepo.findByOrderId(1L)).thenReturn(List.of(new OrderItem()));

        List<Orders> result = service.getMyOrders("token");

        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getItems());
    }

    @Test
    void testUpdateStatus() {
        Orders order = new Orders();
        order.setStatus("PLACED");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenReturn(order);

        Orders result = service.updateStatus(1L, "SHIPPED");

        assertEquals("SHIPPED", result.getStatus());
    }

    @Test
    void testCancelOrder() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        Orders order = new Orders();
        order.setUserId(1L);
        order.setStatus("PLACED");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenReturn(order); // ✅ FIX

        Orders result = service.cancelOrder("token", 1L);

        assertEquals("CANCELLED", result.getStatus());
    }
    
    @Test
    void testOrderNotFound() {

        when(orderRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.updateStatus(1L, "SHIPPED"));
    }
    
    @Test
    void testCannotUpdateDelivered() {

        Orders order = new Orders();
        order.setStatus("DELIVERED");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class,
                () -> service.updateStatus(1L, "SHIPPED"));
    }
    
    @Test
    void testUnauthorizedCancel() {

        when(jwtUtil.extractUserId("token")).thenReturn(2L);

        Orders order = new Orders();
        order.setUserId(1L);
        order.setStatus("PLACED");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class,
                () -> service.cancelOrder("token", 1L));
    }
    
    @Test
    void testCancelDeliveredOrder() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        Orders order = new Orders();
        order.setUserId(1L);
        order.setStatus("DELIVERED");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class,
                () -> service.cancelOrder("token", 1L));
    }
    
    @Test
    void testRefundStatus() {

        Orders order = new Orders();
        order.setStatus("PLACED");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenReturn(order);

        Orders result = service.updateStatus(1L, "REFUNDED");

        assertEquals("REFUNDED", result.getPaymentStatus());
    }
    
    @Test
    void testCheckoutNullCart() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);
        when(cartClient.getCart(any())).thenReturn(null); // ✅ NULL case

        OrderRequest req = new OrderRequest();
        req.setPaymentMethod("COD");

        assertThrows(RuntimeException.class,
                () -> service.checkout("token", req));
    }
    
 
}
