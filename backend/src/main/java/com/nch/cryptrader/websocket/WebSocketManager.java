package com.nch.cryptrader.websocket;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public interface WebSocketManager {
    Mono<Void> registerUserSession(WebSocketSession session);
}
