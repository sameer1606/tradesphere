package com.tradesphere.order.dto;

import com.tradesphere.order.domain.OrderSide;
import com.tradesphere.order.domain.OrderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PlaceOrderRequest(
        @NotNull(message = "accountProfileId is required")
        UUID accountProfileId,
        String symbol,
        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity,
        @NotNull(message = "price is required")
        @Positive(message = "price must be greater than zero")
        BigDecimal price,
        OrderType orderType,
        OrderSide orderSide) {


}
