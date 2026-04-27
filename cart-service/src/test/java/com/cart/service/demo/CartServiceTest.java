package com.cart.service.demo;

import com.cart.service.demo.client.ProductClient;
import com.cart.service.demo.dto.*;
import com.cart.service.demo.entity.CartItem;
import com.cart.service.demo.repository.CartRepository;
import com.cart.service.demo.service.CartService;
import com.cart.service.demo.util.JwtUtil;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository repo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ 1. NEW ITEM (else branch)
    @Test
    void testAddNewItem() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);
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

        CartItem result = service.add("token", req);

        assertNotNull(result);
    }

    // ✅ 2. EXISTING ITEM (if branch)
    @Test
    void testUpdateExistingItem() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        CartItem existing = new CartItem();
        existing.setQuantity(2);

        when(repo.findByUserIdAndProductId(1L, 1L))
                .thenReturn(List.of(existing));

        when(repo.save(any())).thenReturn(existing);

        CartRequest req = new CartRequest();
        req.setProductId(1L);
        req.setQuantity(3);

        CartItem result = service.add("token", req);

        assertEquals(5, result.getQuantity()); // 2 + 3
    }

    // ✅ 3. GET CART
    @Test
    void testGetCart() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);
        when(repo.findByUserId(1L)).thenReturn(List.of(new CartItem()));

        List<CartItem> result = service.get("token");

        assertFalse(result.isEmpty());
    }

    // ✅ 4. REMOVE
    @Test
    void testRemove() {

        service.remove("token", 1L);

        verify(repo).deleteById(1L);
    }

    // ✅ 5. CLEAR
    @Test
    void testClear() {

        when(jwtUtil.extractUserId("token")).thenReturn(1L);

        service.clear("token");

        verify(repo).deleteByUserId(1L);
    }
}