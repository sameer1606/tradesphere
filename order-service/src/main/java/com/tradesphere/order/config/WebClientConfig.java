package com.tradesphere.order.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${client.account-service.base-url}")
    private String accountserviceBaseUrl;
    @Value("${client.account-service.connect-timeout}")
    private Integer connectTimeout;
    @Value("${client.account-service.read-timeout}")
    private Integer readTimeout;

    @Bean
    public WebClient accountServiceWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));

        return WebClient.builder()
                .baseUrl(accountserviceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }
}
