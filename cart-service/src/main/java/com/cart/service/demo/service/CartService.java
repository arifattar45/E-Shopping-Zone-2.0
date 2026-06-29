package com.cart.service.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cart.service.demo.client.ProductClient;
import com.cart.service.demo.dto.CartRequest;
import com.cart.service.demo.dto.Product;
import com.cart.service.demo.entity.CartItem;
import com.cart.service.demo.repository.CartRepository;

@Service
public class CartService {

    private final CartRepository repo;
    private final ProductClient productClient;

    public CartService(CartRepository repo, ProductClient productClient) {
        this.repo = repo;
        this.productClient = productClient;
    }

    public CartItem add(Long userId, CartRequest req) {

        List<CartItem> existing =
            repo.findByUserIdAndProductId(userId, req.getProductId());

        CartItem item;

        if (!existing.isEmpty()) {
            // ✅ update quantity
            item = existing.get(0);
            item.setQuantity(item.getQuantity() + req.getQuantity());
        } else {
            // ✅ create new
            Product product = productClient.getProductById(req.getProductId());

            item = new CartItem();
            item.setUserId(userId);
            item.setProductId(req.getProductId());
            item.setQuantity(req.getQuantity());
            item.setPrice(product.getPrice());
            item.setProductName(product.getName());
            item.setImageUrl(product.getImageUrl());
        }

        return repo.save(item);
    }

    public List<CartItem> get(Long userId) {
        return repo.findByUserId(userId);
    }

    public void remove(Long userId, Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void clear(Long userId) {
        repo.deleteByUserId(userId);
    }
}
