package com.tradesphere.account.exception;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID accountProfileId) {
        super("Insufficient funds for accountProfileId: " + accountProfileId);
    }
}
