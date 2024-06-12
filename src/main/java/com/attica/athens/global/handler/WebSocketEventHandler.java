package com.attica.athens.global.handler;

import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.exception.NotFoundActiveAgoraUserException;
import com.attica.athens.domain.agoraUser.exception.NotFoundAgoraUserException;
import com.attica.athens.global.auth.CustomUserDetails;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final AgoraUserRepository agoraUserRepository;

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        getUserDetailsAndSessionId(event);
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

    @EventListener(SessionConnectedEvent.class)
    public void handleWebSocketSessionConnected() {
        log.info("WebSocket Connected");
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionDisconnectEvent event) {
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

    public void getUserDetailsAndSessionId(SessionConnectEvent event) {

        CustomUserDetails customUserDetails = getCustomUserDetails(event);

        String sessionId = getSessionId(event);

        updateSessionId(customUserDetails, sessionId);
    }

    @Transactional
    public void updateSessionId(CustomUserDetails customUserDetails, String sessionId) {
        Long userId = customUserDetails.getUserId();
        List<AgoraUser> agoraUsers = agoraUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundAgoraUserException(userId));

        AgoraUser agoraUser = AgoraUser.findAgoraUser(agoraUsers)
                .orElseThrow(() -> new NotFoundActiveAgoraUserException());

        agoraUser.updateSessionId(sessionId);
    }

    public String getSessionId(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        return sessionId;
    }

    public CustomUserDetails getCustomUserDetails(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);

        Authentication authentication = (Authentication) Objects.requireNonNull(accessor.getUser());
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails;
    }
}
