package com.order.service.demo.config;

import com.order.service.demo.filter.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                //USER APIs
                .requestMatchers(HttpMethod.POST, "/orders/checkout").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/orders").hasRole("USER")

                //ADMIN APIs
                .requestMatchers(HttpMethod.GET, "/orders/all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/orders/**").hasRole("ADMIN")

                //Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                //Everything else
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}