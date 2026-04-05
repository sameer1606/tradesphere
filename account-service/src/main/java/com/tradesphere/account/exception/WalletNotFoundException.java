package com.tradesphere.account.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(UUID accountProfileId) {
        super("Wallet not found for accountProfileId: " + accountProfileId);
    }
}
