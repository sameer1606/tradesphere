package com.tradesphere.market.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * Configures STOMP over WebSocket for real-time price streaming to Angular.
 *
 * Flow:
 *   Finnhub tick → MarketService → SimpMessagingTemplate → /topic/price/{symbol} → Angular
 *
 * /ws        — endpoint Angular connects to (SockJS fallback for older browsers)
 * /topic     — outbound: server pushes price ticks to Angular subscribers
 * /app       — inbound: Angular sends messages to server (e.g. subscribe to symbol)
 *
 * Actual publishing via SimpMessagingTemplate — wired in MarketService (not here).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        /*Three things happening:
            addEndpoint("/ws") — Angular connects here
            setAllowedOriginPatterns("*") — allow all origins (CORS for WebSocket)
            withSockJS() — fallback for browsers that don't support WebSocket natively
        */
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages from Angular to server go through /app prefix (e.g. /app/trade)
        registry.setApplicationDestinationPrefixes("/tradesphere");
        // Server publishes to /topic — Angular subscribers receive live price ticks
        registry.enableSimpleBroker("/topic");
    }
}
