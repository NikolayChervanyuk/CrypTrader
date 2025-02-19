package com.nch.cryptrader.configuration;

import com.nch.cryptrader.websocket.TickerWebSocketHandler;
import com.nch.cryptrader.websocket.TickerWebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class WebSocketConfig {

    @Bean
    public TickerWebSocketHandler tickerWebSocketHandler(TickerWebSocketManager tickerWebSocketManager) {
        return new TickerWebSocketHandler(tickerWebSocketManager);
    }

    @Bean
    public HandlerMapping handlerMapping(TickerWebSocketHandler tickerWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ticker-ws", tickerWebSocketHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public ReactorNettyWebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}
