package com.attica.athens.global.handler;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
public class WebSocketEventHandler {

    @EventListener
    public void handleWebSocketSessionConnected(SessionConnectedEvent session) {
        log.info("WebSocket Connected: " + Objects.requireNonNull(session.getUser()).getName());
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionUnsubscribeEvent session) {
        log.info("WebSocket Disconnected: " + Objects.requireNonNull(session.getUser()).getName());
    }

    @EventListener
    public void handleWebSocketSessionSubscribe(SessionSubscribeEvent session) {
        log.info("WebSocket Subscribe: " + Objects.requireNonNull(session.getUser()).getName());
    }

    @EventListener
    public void handleWebSocketSessionUnsubscribe(SessionUnsubscribeEvent session) {
        log.info("WebSocket Unsubscribe: " + Objects.requireNonNull(session.getUser()).getName());
    }

    @EventListener
    public void handleWebSocketSessionConnectedEventListener(SessionConnectedEvent session) {
        log.info("WebSocket Connected: " + Objects.requireNonNull(session.getUser()).getName());
    }
}
