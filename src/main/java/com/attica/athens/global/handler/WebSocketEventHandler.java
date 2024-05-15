package com.attica.athens.global.handler;

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
    public void handleWebSocketSessionConnected(SessionConnectedEvent event) {
        log.info("WebSocket Connected: ");
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionUnsubscribeEvent session) {
        log.info("WebSocket Disconnected: ");
    }

    @EventListener
    public void handleWebSocketSessionSubscribe(SessionSubscribeEvent session) {
        log.info("WebSocket Subscribe: ");
    }

    @EventListener
    public void handleWebSocketSessionUnsubscribe(SessionUnsubscribeEvent session) {
        log.info("WebSocket Unsubscribe: ");
    }

    @EventListener
    public void handleWebSocketSessionConnectedEventListener(SessionConnectedEvent session) {
        log.info("WebSocket Connected: ");
    }
}
