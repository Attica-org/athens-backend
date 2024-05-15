package com.attica.athens.global.handler;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
public class WebSocketEventHandler {

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        logConnectEvent(event);
    }

    private static void logConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
        String userRole = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("userRole");

        log.info("WebSocket {}: userId={}, userRole={}", event.getClass().getSimpleName(), userId, userRole);
    }

    @EventListener(SessionConnectedEvent.class)
    public void handleWebSocketSessionConnected() {
        log.info("WebSocket Connected");
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Objects.requireNonNull(accessor.getSessionAttributes()).clear();

        log.info("WebSocket Disconnected");
    }

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebSocketSessionSubscribe() {
        log.info("WebSocket Subscribe");
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void handleWebSocketSessionUnsubscribe() {
        log.info("WebSocket Unsubscribe");
    }
}
