package com.tradesphere.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * TradeSphere — API Gateway
 *
 * Responsibilities:
 *   - JWT validation on inbound requests
 *   - Route dispatch to downstream services
 *   - Rate limiting via Redis
 *   - CORS enforcement
 *
 * NOT responsible for:
 *   - business logic
 *   - DB access
 *   - domain rules
 */
@SpringBootApplication(
        exclude = {
                ReactiveUserDetailsServiceAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}