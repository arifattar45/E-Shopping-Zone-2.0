package com.cart.service.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import com.cart.service.demo.controller.CartController;
import com.cart.service.demo.dto.CartRequest;
import com.cart.service.demo.entity.CartItem;
import com.cart.service.demo.filter.JwtFilter;
import com.cart.service.demo.service.CartService;
import com.cart.service.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	    controllers = CartController.class,
	    excludeFilters = @ComponentScan.Filter(
	        type = FilterType.ASSIGNABLE_TYPE,
	        classes = JwtFilter.class   // 🔥 IMPORTANT
	))
	@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService service;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JwtUtil jwtUtil;
    
    @Test
    void testAddItem() throws Exception {

        when(service.add(any(), any())).thenReturn(new CartItem());

        CartRequest req = new CartRequest();
        req.setProductId(1L);
        req.setQuantity(2);

        mockMvc.perform(post("/cart/add")
                .header("Authorization", "Bearer token")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ✅ GET CART
    @Test
    void testGetCart() throws Exception {

        when(service.get(any())).thenReturn(List.of(new CartItem()));

        mockMvc.perform(get("/cart")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    // ✅ REMOVE ITEM
    @Test
    void testRemoveItem() throws Exception {

        mockMvc.perform(delete("/cart/remove/1")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    // ✅ CLEAR CART
    @Test
    void testClearCart() throws Exception {

        mockMvc.perform(delete("/cart/clear")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
