package com.tradesphere.order.exception;

import com.tradesphere.order.domain.OrderStatus;

import java.util.UUID;

public class OrderNotCancellableException extends RuntimeException {
    public OrderNotCancellableException(UUID orderId, OrderStatus orderStatus) {
        super("Order with orderId: " + orderId + " cannot be cancelled as it is in " + orderStatus + " status");
    }
}
