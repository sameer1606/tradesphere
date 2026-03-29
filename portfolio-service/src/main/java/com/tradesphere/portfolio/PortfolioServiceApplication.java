package com.tradesphere.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TradeSphere — Portfolio Service
 *
 * Source of truth rule (critical):
 *   Durable state (holdings, positions) comes from Kafka order.executed events ONLY.
 *   Redis is used ONLY for transient live P&L recalculation.
 *   Postgres is NEVER written directly from ticks.
 *
 * Data flow:
 *   Kafka order.executed → HoldingsService (CNC) or PositionService (MIS)
 *   Redis price:tick:*   → PriceTickConsumer → recalculate unrealised P&L → Redis cache
 *   @Scheduled 60s       → batch flush P&L from Redis → Postgres
 *   @Scheduled 15:35     → SnapshotJob writes PNL_SNAPSHOT per user
 *
 * WebSocket pushes PNL_UPDATE to /topic/pnl.{userId} on every P&L recalculation.
 */
@SpringBootApplication
@EnableKafka
@EnableScheduling
public class PortfolioServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PortfolioServiceApplication.class, args);
    }
}
