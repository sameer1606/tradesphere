package com.tradesphere.market.provider.twelvedata;

import com.tradesphere.market.domain.OhlcCandle;
import com.tradesphere.market.dto.twelvedata.TwelveDataCandleResponse;
import com.tradesphere.market.exception.MarketDataException;
import com.tradesphere.market.provider.CandleDataProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TwelveDataMarketDataProvider implements CandleDataProvider {
    private final WebClient twelveDataApiClient;
    @Value("${twelvedata.api-key}")
    private String apiKey;

    public TwelveDataMarketDataProvider(@Qualifier("twelveDataWebClient") WebClient webClient) {
        this.twelveDataApiClient = webClient;
    }

    @Override
    public List<OhlcCandle> getCandles(String symbol, Instant from, Instant to, String interval, String exchange) {
        TwelveDataCandleResponse twelveDataCandleResponse =
                twelveDataApiClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/time_series")
                                .queryParam("symbol", symbol)
                                .queryParam("interval", interval)
                                .queryParam("start_date", from.toString())
                                .queryParam("end_date", to.toString())
                                .queryParam("exchange", exchange)
                                .queryParam("apikey", apiKey)
                                .build())
                        .retrieve()
                        .bodyToMono(TwelveDataCandleResponse.class)
                        .block();
        if (twelveDataCandleResponse == null) throw new MarketDataException("Empty response from TwelveData for symbol: " + symbol);
        return twelveDataCandleResponse.values()
                .stream()
                .map(candle -> new OhlcCandle(
                        null,
                        twelveDataCandleResponse.meta().symbol(),     // from meta
                        twelveDataCandleResponse.meta().exchange(),   // from meta
                        twelveDataCandleResponse.meta().interval(),   // from meta
                        new BigDecimal(candle.open()),
                        new BigDecimal(candle.high()),
                        new BigDecimal(candle.low()),
                        new BigDecimal(candle.close()),
                        Long.parseLong(candle.volume()),
                        LocalDate.parse(candle.datetime())
                                .atStartOfDay(ZoneOffset.UTC)
                                .toInstant()
                ))
                .toList();
    }
}
