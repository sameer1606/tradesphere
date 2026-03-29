package com.tradesphere.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * TradeSphere — Identity Service
 *
 * Responsibilities:
 *   - User registration and login
 *   - JWT access token issuance (15 min expiry)
 *   - Refresh token management (7 day expiry, stored hashed in Postgres)
 *   - Token revocation via Redis blacklist (jti → TTL)
 *   - RBAC role assignment
 *
 * NOT responsible for: profile data, watchlists, funds, orders, portfolio.
 *
 * Security note:
 *   accessToken → response body (stored in memory by Angular)
 *   refreshToken → httpOnly cookie (invisible to JavaScript)
 */
@SpringBootApplication
@EnableAsync
public class IdentityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
