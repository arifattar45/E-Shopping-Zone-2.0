package com.cart.service.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cart.service.demo.client.ProductClient;
import com.cart.service.demo.dto.CartRequest;
import com.cart.service.demo.dto.Product;
import com.cart.service.demo.entity.CartItem;
import com.cart.service.demo.repository.CartRepository;
import com.cart.service.demo.util.JwtUtil;

@Service
public class CartService {

    private final CartRepository repo;
    private final JwtUtil jwtUtil;
    private final ProductClient productClient;

    public CartService(CartRepository repo, JwtUtil jwtUtil, ProductClient productClient) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.productClient = productClient;
    }

    public CartItem add(String token, CartRequest req) {

        Long userId = jwtUtil.extractUserId(token);

        Product product;

        try {
            product = productClient.getProductById(req.getProductId());
        } catch (Exception e) {
            throw new RuntimeException("Product not found with id: " + req.getProductId());
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(req.getProductId());
        item.setQuantity(req.getQuantity());
        item.setPrice(product.getPrice());
        item.setProductName(product.getName());

        return repo.save(item);
    }

    public List<CartItem> get(String token) {
        Long userId = jwtUtil.extractUserId(token);
        
        return repo.findByUserId(userId);
    }

    public void remove(String token, Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void clear(String token) {
        Long userId = jwtUtil.extractUserId(token);
        repo.deleteByUserId(userId);
    }
}