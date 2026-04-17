package com.tradesphere.market.exception;

public record ErrorResponse(String errorCode, String errorMsg, String timestamp) {
}
