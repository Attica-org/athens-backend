package com.attica.athens.global.handler;

import com.attica.athens.domain.agoraUser.application.AgoraUserService;
import com.attica.athens.global.auth.CustomUserDetails;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final AgoraUserService agoraUserService;

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        logConnectEvent(event);
    }

    private void logConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        Authentication authentication = (Authentication) Objects.requireNonNull(accessor.getUser());
        String username = authentication.getName();
        String userRole = getUserRole(authentication);

        log.debug("WebSocket {}: username={}, userRole={}", event.getClass().getSimpleName(), username, userRole);
    }

    private String getUserRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalArgumentException("User role is not exist."));
    }

    @EventListener
    public void handleWebSocketSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");

        if (accessor.getUser() != null && nativeHeaders.containsKey("AgoraId")) {
            Long userId = getUserId(accessor);
            Long agoraId = getAgoraId(nativeHeaders);
            String sessionId = (String) generic.getHeaders().get("simpSessionId");

            agoraUserService.updateSessionId(agoraId, userId, sessionId);
            agoraUserService.sendMetaToActiveUsers(agoraId);

            log.info("SessionId updated: agoraId={}, userId={}, sessionId={}", agoraId, userId, sessionId);
        }
        log.debug("WebSocket Connected");
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    private long getAgoraId(Map nativeHeaders) {
        return Long.parseLong((String) ((List<?>) nativeHeaders.get("AgoraId")).get(0));
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Long agoraId = agoraUserService.findAgoraIdBySessionId(sessionId);
        Long userId = getUserId(StompHeaderAccessor.wrap(event.getMessage()));

        agoraUserService.removeSessionId(sessionId);
        agoraUserService.sendMetaToActiveUsers(agoraId);

        log.info("WebSocket Disconnected: sessionId={}, agoraId={}, userId={}", sessionId, agoraId, userId);
    }

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebSocketSessionSubscribe() {
        log.debug("WebSocket Subscribe");
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void handleWebSocketSessionUnsubscribe() {
        log.debug("WebSocket Unsubscribe");
    }
}
