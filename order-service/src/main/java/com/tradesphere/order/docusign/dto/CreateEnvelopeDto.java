package com.tradesphere.order.docusign.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateEnvelopeDto(UUID orderId, String recipientEmail, String recipientName, BigDecimal orderAmount) {
}
