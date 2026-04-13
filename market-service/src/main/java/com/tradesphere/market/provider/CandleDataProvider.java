package com.tradesphere.market.provider;

import com.tradesphere.market.domain.OhlcCandle;

import java.time.Instant;
import java.util.List;

public interface CandleDataProvider {
    List<OhlcCandle> getCandles(String symbol, Instant from, Instant to, String interval, String exchange);

}
