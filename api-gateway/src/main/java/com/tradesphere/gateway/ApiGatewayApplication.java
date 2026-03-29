package com.tradesphere.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TradeSphere — API Gateway
 *
 * Responsibilities:
 *   - JWT validation on every inbound request
 *   - Route dispatch to downstream services
 *   - Rate limiting via Redis token bucket
 *   - CORS enforcement
 *   - Correlation ID injection (X-Correlation-ID header)
 *
 * NOT responsible for: business logic, DB access, domain rules.
 * Spring Cloud Gateway is reactive (WebFlux) — do NOT mix servlet annotations here.
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
