package com.nch.cryptrader.state;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.UUID;


@Getter
@Setter
@Component
public class WebSocketSessions {
    private final HashMap<UUID, WebSocketSession> sessions = new HashMap<>();
}
