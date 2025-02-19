package com.nch.cryptrader.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class TickerWebSocketHandler implements WebSocketHandler {

    private final TickerWebSocketManager webSocketManager;

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        return webSocketManager.registerUserSession(session);
    }
}
