package com.tradesphere.account.exception;

import java.util.UUID;

public class DuplicateReservationConflictException extends RuntimeException {
    public DuplicateReservationConflictException(UUID orderId) {
        super("Active reservation with different amount already exists for orderId: " + orderId);    }
}
