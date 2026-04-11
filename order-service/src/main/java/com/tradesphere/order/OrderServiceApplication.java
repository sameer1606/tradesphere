package com.tradesphere.order;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TradeSphere — Order Service
 *
 * Order placement flow:
 *   1. IdempotencyGuard    — check X-Idempotency-Key in Redis (NX SET)
 *   2. OrderValidator      — cross-field validation, market status, symbol check
 *   3. FundsClient (Feign) — POST /internal/funds/reserve → Account Service
 *   4. DB write            — save Order(PENDING) + OutboxEvent in one @Transactional
 *   5. ExecutionStrategy   — async: MARKET fills immediately, LIMIT registers in watcher
 *   6. On fill             — Order → EXECUTED, Trade saved, OutboxEvent written
 *   7. OutboxEventRelay    — @Scheduled 500ms — unpublished rows → Kafka
 *
 * Feign clients:
 *   FundsClient  → Account Service /internal/funds/*
 *   MarketClient → Market Service  /api/market/*
 *
 * Kafka published (via Outbox): order.executed · order.cancelled · order.rejected
 * Redis Pub/Sub consumed: price:tick:{symbol} → LimitOrderWatcher triggers fills
 * MIS auto square-off: @Scheduled cron "0 20 15 * * MON-FRI"
 */
@OpenAPIDefinition(
        info = @Info(
                title = "TradeSphere Order Service",
                version = "1.0",
                description = "Handles order placement and cancellation"
        )
)
@SpringBootApplication
@EnableKafka
@EnableAsync
@EnableScheduling
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
