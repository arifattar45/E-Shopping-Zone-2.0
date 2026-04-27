package com.product.service.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.service.demo.controller.ProductController;
import com.product.service.demo.dto.ProductRequest;
import com.product.service.demo.entity.Product;
import com.product.service.demo.service.ProductService;
import com.product.service.demo.util.JwtUtil;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper mapper;
    
    
    @MockBean
    private JwtUtil jwtUtil;  
    
    
    @Test
    void testAdd() throws Exception {
        when(service.add(any())).thenReturn(new Product());

        ProductRequest req = new ProductRequest();
        req.setName("Phone");

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(new Product())));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        when(service.getById(1L)).thenReturn(new Product());

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testUpdate() throws Exception {

        when(service.update(anyLong(), any()))
                .thenReturn(new Product());

        ProductRequest req = new ProductRequest();
        req.setName("Updated");

        mockMvc.perform(put("/products/1")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());
    }
}
