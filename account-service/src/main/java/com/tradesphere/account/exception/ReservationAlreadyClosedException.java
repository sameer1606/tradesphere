package com.tradesphere.account.exception;

import java.util.UUID;

public class ReservationAlreadyClosedException extends RuntimeException {
    public ReservationAlreadyClosedException(UUID orderId) {
        super("Reservation already closed for orderId: " + orderId);
    }
}
