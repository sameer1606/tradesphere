package com.tradesphere.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ReserveFundsRequest(UUID accountProfileId, UUID orderId, BigDecimal amount) {
}
