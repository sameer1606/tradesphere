package com.tradesphere.market.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceTick(String symbol, BigDecimal price, Instant timestamp, Long volume) {
}
