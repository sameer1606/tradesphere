package com.tradesphere.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.order.client.FundsClient;
import com.tradesphere.order.domain.Order;
import com.tradesphere.order.domain.OrderStatus;
import com.tradesphere.order.dto.PlaceOrderRequest;
import com.tradesphere.order.exception.OrderNotFoundException;
import com.tradesphere.order.guard.OrderGuard;
import com.tradesphere.order.outbox.EventStatus;
import com.tradesphere.order.outbox.OutboxEvent;
import com.tradesphere.order.outbox.OutboxEventRepository;
import com.tradesphere.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;
    final FundsClient fundsClient;
    final OutboxEventRepository outboxEventRepository;
    final ObjectMapper objectMapper;
    final OrderGuard orderGuard;

    @Transactional
    public void placeOrder(PlaceOrderRequest request) {
        Order order = Order.builder()
                .accountProfileId(request.accountProfileId())
                .symbol(request.symbol())
                .quantity(request.quantity())
                .price(request.price())
                .orderStatus(OrderStatus.NEW)
                .side(request.orderSide())
                .orderType(request.orderType())
                .build();
        order = orderRepository.save(order);
        try {
            fundsClient.reserveFunds(request.accountProfileId(), order.getOrderId(), request.price().multiply(BigDecimal.valueOf(request.quantity())));
            order.setOrderStatus(OrderStatus.EXECUTED);
            OutboxEvent outboxEvent = null;
            try {
                outboxEvent = OutboxEvent.builder()
                        .orderID(order.getOrderId())
                        .payload(objectMapper.writeValueAsString(Map.of("orderId", order.getOrderId())))
                        .eventStatus(EventStatus.PENDING)
                        .topic("order.executed")
                        .build();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            outboxEventRepository.save(outboxEvent);

        } catch (Exception e) {
            order.setOrderStatus(OrderStatus.REJECTED);

            OutboxEvent outboxEvent = null;
            try {
                outboxEvent = OutboxEvent.builder()
                        .orderID(order.getOrderId())
                        .payload(objectMapper.writeValueAsString(Map.of("orderId", order.getOrderId())))
                        .eventStatus(EventStatus.PENDING)
                        .topic("order.rejected")
                        .build();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            outboxEventRepository.save(outboxEvent);
        }
        orderRepository.save(order);

    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order orderRecord = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        orderGuard.validateCancellableOrder(orderRecord);
        orderRecord.setOrderStatus(OrderStatus.CANCELLED);
        OutboxEvent outboxEvent = null;
        try {
            outboxEvent = OutboxEvent.builder()
                    .orderID(orderRecord.getOrderId())
                    .payload(objectMapper.writeValueAsString(Map.of("orderId", orderRecord.getOrderId())))
                    .eventStatus(EventStatus.PENDING)
                    .topic("order.cancelled")
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        outboxEventRepository.save(outboxEvent);
        orderRepository.save(orderRecord);

    }
}
