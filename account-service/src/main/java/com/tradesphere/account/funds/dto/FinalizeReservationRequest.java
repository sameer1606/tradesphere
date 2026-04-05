package com.tradesphere.account.funds.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FinalizeReservationRequest(

        @NotNull(message = "orderId is required")
        UUID orderId
) {}