package com.attica.athens.global.handler;

import com.attica.athens.domain.agoraUser.application.AgoraUserService;
import com.attica.athens.domain.chat.application.ChatQueryService;
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
    private final ChatQueryService chatQueryService;

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        logConnectEvent(event);
    }

    private void logConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);

        Authentication authentication = (Authentication) Objects.requireNonNull(accessor.getUser());
        String username = authentication.getName();
        String userRole = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalArgumentException("User role is not exist."));

        log.info("WebSocket {}: username={}, userRole={}", event.getClass().getSimpleName(), username, userRole);
    }

    @EventListener
    public void handleWebSocketSessionConnected(SessionConnectedEvent event) {
        log.info("WebSocket Connected");

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");

        if (accessor.getUser() != null && nativeHeaders.containsKey("agoraId")) {
            Long userId = getUserId(accessor);
            Long agoraId = Long.parseLong((String) ((List<?>) nativeHeaders.get("agoraId")).get(0));
            String sessionName = accessor.getSessionId();
            agoraUserService.updateSessionName(agoraId, userId, sessionName);

            log.info("SessionName updated: agoraId={}, userId={}, sessionName={}", agoraId, userId, sessionName);
        }
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        agoraUserService.removeSessionName(accessor.getSessionId());

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
