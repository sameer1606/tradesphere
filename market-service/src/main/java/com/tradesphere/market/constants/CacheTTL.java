package com.tradesphere.market.constants;

import java.time.Duration;

public final class CacheTTL {
    public static final Duration SEARCH = Duration.ofDays(1);
    public static final Duration QUOTE = Duration.ofSeconds(30);
    public static final Duration CANDLES = Duration.ofMinutes(5);

    private CacheTTL() {}
}