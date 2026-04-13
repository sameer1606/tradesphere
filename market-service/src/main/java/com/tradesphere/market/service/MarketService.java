package com.tradesphere.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.market.domain.PriceTickEvent;
import com.tradesphere.market.exception.MarketDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    @Async
    @EventListener
    public void onPriceTick(PriceTickEvent priceTickEvent) {
        try {
            var priceTick = priceTickEvent.getPriceTick();
            String value = objectMapper.writeValueAsString(priceTick);

            //Redis cache
            String key = "price:" + priceTick.symbol();
            Duration duration = Duration.ofSeconds(30);
            stringRedisTemplate.opsForValue().set(key, value, duration);
            //Redis Pub Sub
            String channel = "price:tick:" + priceTick.symbol();
            stringRedisTemplate.convertAndSend(channel, value);
            //WebSocket
            simpMessagingTemplate.convertAndSend("/topic/price/" + priceTick.symbol(), value);

        } catch (JsonProcessingException e) {
            throw new MarketDataException(e);
        }
    }
}
