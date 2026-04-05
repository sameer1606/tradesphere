package com.tradesphere.account.funds.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ReserveFundsRequest(

        @NotNull(message = "accountProfileId is required")
        UUID accountProfileId,

        @NotNull(message = "orderId is required")
        UUID orderId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        BigDecimal amount
) {}