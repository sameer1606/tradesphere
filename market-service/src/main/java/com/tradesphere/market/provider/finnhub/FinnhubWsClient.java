package com.tradesphere.market.provider.finnhub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.market.domain.PriceTick;
import com.tradesphere.market.domain.PriceTickEvent;
import com.tradesphere.market.dto.finnhub.FinnhubTradeResponse;
import com.tradesphere.market.exception.MarketDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FinnhubWsClient extends AbstractWebSocketHandler {
    @Value("${finnhub.default.symbols}")
    private List<String> symbols;
    @Value("${finnhub.api-key}")
    private String apiKey;
    @Value("${finnhub.websocket-url}")
    private String wsUrl;

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void connectOnApplicationReadyEvent(ApplicationReadyEvent event) {
        StringBuilder url = new StringBuilder(wsUrl);
        url.append("?token=").append(apiKey);
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                this,
                url.toString());
        manager.setAutoStartup(true);
        manager.start();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        FinnhubTradeResponse response = objectMapper.readValue(payload, FinnhubTradeResponse.class);
        if(response.type().equalsIgnoreCase("trade")){
            response.data().forEach(trade -> {
                PriceTick priceTick = new PriceTick(
                        trade.symbol(),
                        trade.price(),
                        Instant.ofEpochMilli(trade.timestamp()),
                        trade.volume()
                );
                applicationEventPublisher.publishEvent(new PriceTickEvent(this, priceTick));
            });
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        symbols.forEach(symbol -> {
            try {
                String request = "{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}";
                session.sendMessage(new TextMessage(request));
            } catch (IOException e) {
                throw new MarketDataException("Failed to subscribe to symbol: " + symbol, e);
            }
        });
    }
}
