package com.tradesphere.market.provider;

import com.tradesphere.market.domain.Instrument;
import com.tradesphere.market.domain.OhlcCandle;
import com.tradesphere.market.domain.PriceTick;

import java.time.Instant;
import java.util.List;

public interface MarketDataProvider {
    PriceTick getQuote(String symbol);
    List<Instrument> searchInstruments(String query);
}
