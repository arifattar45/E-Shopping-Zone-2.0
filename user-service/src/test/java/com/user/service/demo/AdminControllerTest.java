package com.user.service.demo;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.user.service.demo.controller.AdminController;
import com.user.service.demo.entity.User;
import com.user.service.demo.repository.UserRepository;
import com.user.service.demo.util.JwtUtil;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository repo;
    
    @MockBean
    private JwtUtil jwtUtil; 

    @Test
    void testGetUsers() throws Exception {
        when(repo.findAll()).thenReturn(List.of(new User()));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/admin/delete/1"))
                .andExpect(status().isOk());

        verify(repo).deleteById(1L);
    }

    @Test
    void testPromoteUser() throws Exception {
        User user = new User();
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(put("/admin/promote/1"))
                .andExpect(status().isOk());

        verify(repo).save(user);
    }
}