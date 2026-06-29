package com.user.service.demo;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.demo.controller.AuthController;
import com.user.service.demo.dto.LoginRequest;
import com.user.service.demo.dto.LoginResponse;
import com.user.service.demo.dto.RegisterRequest;
import com.user.service.demo.repository.UserRepository;
import com.user.service.demo.service.UserService;
import com.user.service.demo.util.JwtUtil; 

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) 
@Import(TestSecurityConfig.class) 
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private UserRepository repo;
    
    @MockBean
    private JwtUtil jwtUtil;
    @Test
    void testRegister() throws Exception {

        when(service.register(any(RegisterRequest.class)))
                .thenReturn("User Registered");

        RegisterRequest req = new RegisterRequest();
        req.name = "Arif";
        req.email = "a@gmail.com";
        req.password = "123456";

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testLogin() throws Exception {

        when(service.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("msg", "token"));

        LoginRequest req = new LoginRequest();
        req.setEmail("a@gmail.com");
        req.setPassword("123");

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
