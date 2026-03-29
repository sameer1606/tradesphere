package com.tradesphere.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * TradeSphere — Notification Service
 *
 * Purely reactive. No outbound calls. No state mutation in other services.
 *
 * Consumes Kafka (group: tradesphere-notification-group):
 *   order.executed  → push "Order executed at ₹X" to Angular
 *   order.cancelled → push "Order cancelled" to Angular
 *   order.rejected  → push "Order rejected: {reason}" to Angular
 *
 * On each event:
 *   1. Build NotificationPayload
 *   2. Persist to MongoDB (append-only log)
 *   3. WebSocketNotifier.convertAndSendToUser(userId, /topic/orders, payload)
 *
 * No Postgres. No Feign. No outbound Kafka events.
 */
@SpringBootApplication
@EnableKafka
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
