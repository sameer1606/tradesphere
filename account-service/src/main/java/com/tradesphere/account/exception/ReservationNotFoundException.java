package com.tradesphere.account.exception;

import java.util.UUID;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(UUID orderId) {
        super("Reservation not found for orderId: " + orderId);
    }
}