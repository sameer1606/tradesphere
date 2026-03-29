package com.tradesphere.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * TradeSphere — Account Service
 *
 * Internal module structure:
 *   com.tradesphere.account.profile     — user profile, preferences
 *   com.tradesphere.account.watchlist   — multiple watchlists per user
 *   com.tradesphere.account.funds       — available/blocked funds, reservation/release
 *
 * The funds module is the extraction boundary.
 * All other services interact with funds via FundsClient (Feign interface).
 *
 * Kafka consumers (funds module):
 *   order.executed  → finalize fund reservation
 *   order.cancelled → release full reservation
 *   order.rejected  → release full reservation
 *
 * Internal endpoints (NOT exposed via Gateway):
 *   POST /internal/funds/reserve
 *   POST /internal/funds/release
 *   POST /internal/funds/credit
 */
@SpringBootApplication
@EnableKafka
@EnableAsync
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
