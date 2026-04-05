package com.tradesphere.account.funds.consumer;

import com.tradesphere.account.funds.event.OrderEvent;
import com.tradesphere.account.funds.service.FundsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    final FundsService fundsService;

    @KafkaListener(topics = {"order.executed", "order.cancelled", "order.rejected"}, groupId = "account-funds-group")
    public void consume(OrderEvent orderEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received Kafka event topic={}, orderId={}", topic, orderEvent.getOrderId());
        switch (topic) {
            case "order.executed" -> fundsService.finalizeReservation(orderEvent.getOrderId());
            case "order.cancelled", "order.rejected" -> fundsService.release(orderEvent.getOrderId());
            default -> log.warn("Received message for unknown topic: {}, ignoring", topic);
        }

    }
}
