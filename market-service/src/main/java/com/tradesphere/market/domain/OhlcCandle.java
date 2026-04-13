package com.tradesphere.market.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record OhlcCandle(String symbol, String exchange, String interval, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Long volume, Instant timestamp) {
}
