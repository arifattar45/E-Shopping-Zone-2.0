package com.product.service.demo;


import com.product.service.demo.entity.Product;
import com.product.service.demo.repository.ProductRepository;
import com.product.service.demo.service.ProductService;
import com.product.service.demo.dto.ProductRequest;
import com.product.service.demo.exception.ProductNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct() {
        ProductRequest req = new ProductRequest();
        req.setName("Phone");
        req.setPrice(1000.0);
        req.setCategory("Electronics");

        when(repo.save(any(Product.class))).thenReturn(new Product());

        Product result = service.add(req);

        assertNotNull(result);
        verify(repo).save(any(Product.class));
    }

    @Test
    void testGetByIdSuccess() {
        Product p = new Product();
        p.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Product result = service.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetByIdFail() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void testDelete() {
        Product p = new Product();
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        service.delete(1L);

        verify(repo).delete(p);
    }

    @Test
    void testUpdate() {
        Product p = new Product();
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        ProductRequest req = new ProductRequest();
        req.setName("Updated");

        when(repo.save(any())).thenReturn(p);

        Product result = service.update(1L, req);

        assertEquals("Updated", result.getName());
    }

    @Test
    void testGetAllSuccess() {
        Page<Product> page = new PageImpl<>(List.of(new Product()));

        when(repo.searchProducts(any(), any(), any(), any(), any()))
                .thenReturn(page);

        Page<Product> result = service.getAll(null, null, null, null, null, 0, 5);

        assertFalse(result.isEmpty());
    }
}
