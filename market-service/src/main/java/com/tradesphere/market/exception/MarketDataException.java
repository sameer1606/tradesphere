package com.tradesphere.market.exception;

public class MarketDataException extends RuntimeException {
    /*Three constructors:
    Message only — when you know what went wrong
    Message + cause — when you're wrapping a lower level exception — most common
    Cause only — when the original exception says enough
    */
    public MarketDataException(String message) {
        super(message);
    }
    public MarketDataException(String message, Throwable cause) {
        super(message, cause);
    }
    public MarketDataException(Throwable cause) {
        super(cause);
    }
}
