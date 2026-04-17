package com.tradesphere.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.market.constants.CacheKeys;
import com.tradesphere.market.constants.CacheTTL;
import com.tradesphere.market.domain.Instrument;
import com.tradesphere.market.domain.PriceTick;
import com.tradesphere.market.domain.PriceTickEvent;
import com.tradesphere.market.exception.MarketDataException;
import com.tradesphere.market.provider.finnhub.FinnhubMarketDataProvider;
import com.tradesphere.market.repository.RedisTickStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final RedisTickStore redisTickStore;
    private final FinnhubMarketDataProvider  finnhubMarketDataProvider;

    @Async
    @EventListener
    public void onPriceTick(PriceTickEvent priceTickEvent) {
        try {
            var priceTick = priceTickEvent.getPriceTick();
            String value = objectMapper.writeValueAsString(priceTick);

            //Redis cache
            String key = CacheKeys.PRICE_PREFIX + priceTick.symbol();
            stringRedisTemplate.opsForValue().set(key, value, CacheTTL.QUOTE);
            //Redis Pub Sub
            String channel = CacheKeys.PRICE_TICK_PREFIX + priceTick.symbol();
            stringRedisTemplate.convertAndSend(channel, value);
            //WebSocket
            simpMessagingTemplate.convertAndSend(CacheKeys.STOMP_PRICE_TOPIC + priceTick.symbol(), value);

        } catch (JsonProcessingException e) {
            throw new MarketDataException(e);
        }
    }

    public List<Instrument> searchInstruments(String query) {
        //Cache Aside
        Optional<String> searchResult = redisTickStore.getSearchResults(query);
        if (searchResult.isPresent()) {
            try {
                return objectMapper.readValue(searchResult.get(), new TypeReference<List<Instrument>>() {
                });
            } catch (JsonProcessingException e) {
                throw new MarketDataException(e);
            }
        }
        List<Instrument> intrumetList = finnhubMarketDataProvider.searchInstruments(query);
        try {
            redisTickStore.saveSearchResults(query, objectMapper.writeValueAsString(intrumetList));
        } catch (JsonProcessingException e) {
            throw new MarketDataException(e);
        }
        return intrumetList;
    }

    public PriceTick getQuote(String symbol) {
        //Cache Aside
        Optional<String> quoteResult = redisTickStore.getQuote(symbol);
        if (quoteResult.isPresent()) {
            try {
                return objectMapper.readValue(quoteResult.get(), PriceTick.class);
            } catch (JsonProcessingException e) {
                throw new MarketDataException(e);
            }
        }
        PriceTick priceTick = finnhubMarketDataProvider.getQuote(symbol);
        try {
            redisTickStore.saveQuote(symbol, objectMapper.writeValueAsString(priceTick));
        } catch (JsonProcessingException e) {
            throw new MarketDataException(e);
        }
        return priceTick;
    }
}
