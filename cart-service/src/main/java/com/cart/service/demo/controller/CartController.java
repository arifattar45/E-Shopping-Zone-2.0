package com.cart.service.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cart.service.demo.dto.CartRequest;
import com.cart.service.demo.entity.CartItem;
import com.cart.service.demo.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart APIs", description = "Operations related to shopping cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    //ADD ITEM
    @Operation(summary = "Add item to cart", description = "Adds a product to user's cart using JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/add")
    public ResponseEntity<CartItem> add(
            @RequestHeader("Authorization") String header,
            @Valid @RequestBody CartRequest request
    ) {
        String token = header.substring(7);
        return ResponseEntity.ok(service.add(token, request));
    }

    //GET CART
    @Operation(summary = "Get user cart", description = "Fetch all cart items for logged-in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(
            @RequestHeader("Authorization") String header
    ) {
        String token = header.substring(7);
        return ResponseEntity.ok(service.get(token));
    }

    //REMOVE ITEM
    @Operation(summary = "Remove item from cart", description = "Deletes a specific item from cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> remove(
            @RequestHeader("Authorization") String header,
            @PathVariable Long id
    ) {
        service.remove(header.substring(7), id);
        return ResponseEntity.ok("Item removed successfully");
    }

    //CLEAR CART
    @Operation(summary = "Clear cart", description = "Removes all items from user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully")
    })
    @DeleteMapping("/clear")
    public ResponseEntity<String> clear(
            @RequestHeader("Authorization") String header
    ) {
        service.clear(header.substring(7));
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
