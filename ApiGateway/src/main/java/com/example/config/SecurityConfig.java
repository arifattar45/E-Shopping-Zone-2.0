package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.example.security.JwtAuthenticationFilter;
import com.example.util.JwtUtil;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    
    
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
    }
    
    

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

    	System.out.println("========== GATEWAY SECURITY LOADED ==========");
    	
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .authorizeExchange(exchange -> exchange

                        // CORS preflight
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        // Public APIs
                        .pathMatchers(
                                "/auth/**",
                                "/user-service/auth/**",
                                "/eureka/**",
                                "/v3/api-docs/**",
                                "/*/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/*/swagger-ui/**",
                                "/swagger-ui.html",
                                "/*/swagger-ui.html",
                                "/webjars/**",
                                "/*/webjars/**",
                                "/actuator/health",
                                "/*/actuator/health"
                        ).permitAll()
                        .pathMatchers(HttpMethod.GET, "/products/**", "/product-service/products/**").permitAll()

                        // Admin APIs
                        .pathMatchers("/admin/**", "/user-service/admin/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/products/**", "/product-service/products/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/products/**", "/product-service/products/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/products/**", "/product-service/products/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/orders/all", "/order-service/orders/all").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/orders/dashboard", "/order-service/orders/dashboard").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/orders/*/status", "/order-service/orders/*/status").hasRole("ADMIN")

                        // User APIs
                        .pathMatchers(HttpMethod.POST, "/orders/checkout", "/order-service/orders/checkout").hasRole("USER")
                        .pathMatchers(HttpMethod.GET, "/orders", "/order-service/orders").hasRole("USER")
                        .pathMatchers(HttpMethod.PUT, "/orders/*/cancel", "/order-service/orders/*/cancel").hasRole("USER")
                        	
                        .pathMatchers(
                                HttpMethod.POST,
                                "/wallet/recharge/**",
                                "/wallet-service/wallet/recharge/**"
                        ).hasRole("USER")
                        
                        .pathMatchers(HttpMethod.GET,
                                "/wallet/**",
                                "/wallet-service/wallet/**")
                        .hasRole("USER")

                        .pathMatchers(HttpMethod.POST,
                                "/wallet/**",
                                "/wallet-service/wallet/**")
                        .hasRole("USER")
                        // Everything else requires login
                        .anyExchange().authenticated()
                )

                .build();
    }
}
