package com.cart.service.demo;

import com.cart.service.demo.client.ProductClient;
import com.cart.service.demo.dto.CartRequest;
import com.cart.service.demo.dto.Product;
import com.cart.service.demo.entity.CartItem;
import com.cart.service.demo.repository.CartRepository;
import com.cart.service.demo.service.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceTest {

    @Mock
    private CartRepository repo;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewItem() {
        when(repo.findByUserIdAndProductId(1L, 1L)).thenReturn(new ArrayList<>());

        Product p = new Product();
        p.setId(1L);
        p.setName("Phone");
        p.setPrice(100.0);

        when(productClient.getProductById(1L)).thenReturn(p);
        when(repo.save(any())).thenReturn(new CartItem());

        CartRequest req = new CartRequest();
        req.setProductId(1L);
        req.setQuantity(2);

        CartItem result = service.add(1L, req);

        assertNotNull(result);
    }

    @Test
    void testUpdateExistingItem() {
        CartItem existing = new CartItem();
        existing.setQuantity(2);

        when(repo.findByUserIdAndProductId(1L, 1L)).thenReturn(List.of(existing));
        when(repo.save(any())).thenReturn(existing);

        CartRequest req = new CartRequest();
        req.setProductId(1L);
        req.setQuantity(3);

        CartItem result = service.add(1L, req);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void testGetCart() {
        when(repo.findByUserId(1L)).thenReturn(List.of(new CartItem()));

        List<CartItem> result = service.get(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void testRemove() {
        service.remove(1L, 1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void testClear() {
        service.clear(1L);

        verify(repo).deleteByUserId(1L);
    }
}
