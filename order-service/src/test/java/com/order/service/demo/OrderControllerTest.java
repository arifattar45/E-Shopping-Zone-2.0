package com.order.service.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.demo.controller.OrderController;
import com.order.service.demo.dto.OrderRequest;
import com.order.service.demo.entity.Orders;
import com.order.service.demo.filter.JwtFilter;
import com.order.service.demo.service.OrderService;
import com.order.service.demo.util.JwtUtil;

@WebMvcTest(controllers = OrderController.class,
excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = JwtFilter.class
))
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false"
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Test
    void testCheckout() throws Exception {

        when(service.checkout(any(), any())).thenReturn(new Orders());

        OrderRequest req = new OrderRequest();
        req.setPaymentMethod("COD");
        req.setAddress("Bangalore"); 

        mockMvc.perform(post("/orders/checkout")
                .header("Authorization", "Bearer token")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetOrders() throws Exception {

        when(service.getMyOrders(any())).thenReturn(java.util.List.of(new Orders()));

        mockMvc.perform(get("/orders")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllOrders() throws Exception {

        when(service.getAllOrders()).thenReturn(java.util.List.of(new Orders()));

        mockMvc.perform(get("/orders/all"))
                .andExpect(status().isOk());
    }
}
