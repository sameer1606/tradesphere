package com.tradesphere.order.service;

import com.tradesphere.order.client.FundsClient;
import com.tradesphere.order.domain.Order;
import com.tradesphere.order.domain.OrderStatus;
import com.tradesphere.order.dto.PlaceOrderRequest;
import com.tradesphere.order.outbox.EventStatus;
import com.tradesphere.order.outbox.OutboxEvent;
import com.tradesphere.order.outbox.OutboxEventRepository;
import com.tradesphere.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;
    final FundsClient fundsClient;
    final OutboxEventRepository outboxEventRepository;
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
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .orderID(order.getOrderId())
                    .payload("{\"orderId\": \"" + order.getOrderId() + "\"}")
                    .eventStatus(EventStatus.PENDING)
                    .topic("order.executed")
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            order.setOrderStatus(OrderStatus.REJECTED);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .orderID(order.getOrderId())
                    .payload("{\"orderId\": \"" + order.getOrderId() + "\"}")
                    .eventStatus(EventStatus.PENDING)
                    .topic("order.rejected")
                    .build();
            outboxEventRepository.save(outboxEvent);
        }
        orderRepository.save(order);

    }
}
