package com.tradesphere.market.provider.finnhub;

import com.tradesphere.market.domain.Instrument;
import com.tradesphere.market.domain.PriceTick;
import com.tradesphere.market.dto.finnhub.FinnhubInstrument;
import com.tradesphere.market.dto.finnhub.FinnhubQuoteResponse;
import com.tradesphere.market.dto.finnhub.FinnhubSearchResponse;
import com.tradesphere.market.exception.MarketDataException;
import com.tradesphere.market.provider.MarketDataProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.time.Instant;
import java.util.List;

@Service
public class FinnhubMarketDataProvider implements MarketDataProvider {
    private final WebClient webClient;
    @Value("${finnhub.api-key}")
    private String apiKey;

    public FinnhubMarketDataProvider(@Qualifier("finnhubWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public PriceTick getQuote(String symbol) {
        FinnhubQuoteResponse res = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubQuoteResponse.class)
                .block();
        if (res == null) throw new MarketDataException("Empty response from Finnhub for symbol: " + symbol);
        PriceTick priceTick = new PriceTick(
                symbol,
                res.currentPrice(),
                Instant.ofEpochSecond(res.timestamp()),
                0L
        );
        return priceTick;
    }

    @Override
    public List<Instrument> searchInstruments(String query) {
        FinnhubSearchResponse finnhubSearchResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubSearchResponse.class)
                .block();
        if (finnhubSearchResponse == null) throw new MarketDataException("Empty response from Finnhub for search query: " + query);
        return finnhubSearchResponse.result()
                .stream()
                .map(fi -> new Instrument(
                        fi.symbol(),
                        fi.description(),
                        fi.type(),
                        fi.displaySymbol()
                ))
                .toList();
    }
}
