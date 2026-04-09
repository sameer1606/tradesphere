package com.tradesphere.order.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderId) {

        super("Order for orderId: " + orderId + " not found");
    }
}
