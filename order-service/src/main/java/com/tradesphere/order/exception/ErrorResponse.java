package com.tradesphere.order.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, String timestamp){
}
