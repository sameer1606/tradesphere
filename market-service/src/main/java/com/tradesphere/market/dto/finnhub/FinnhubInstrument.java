package com.tradesphere.market.dto.finnhub;

public record FinnhubInstrument(
        String symbol,
        String description,
        String type,
        String displaySymbol
) {
}
