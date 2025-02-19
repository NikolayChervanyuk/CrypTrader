package com.nch.cryptrader.websocket;

import com.nch.cryptrader.state.WebSocketSessions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class TickerWebSocketManager implements WebSocketManager {

    private final WebSocketSessions clientWebSocketSessions;

    @Override
    public Mono<Void> registerUserSession(WebSocketSession session) {
        final var sessionId = UUID.randomUUID();
        clientWebSocketSessions.getSessions().put(sessionId, session);

        log.info("New websocket client connection");

        return session.receive().doOnNext(WebSocketMessage::retain).then()
                .doOnError(exception -> log.error(exception.getMessage(), exception))
                .doFinally(v -> clientWebSocketSessions.getSessions().remove(sessionId));
    }
}
