package com.tradesphere.market.domain;

public record Instrument(
        String symbol,
        String description,
        String type,
        String displaySymbol
) {
}
