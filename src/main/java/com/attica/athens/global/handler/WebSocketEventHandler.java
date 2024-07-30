package com.attica.athens.global.handler;

import com.attica.athens.domain.agoraMember.application.AgoraMemberService;
import com.attica.athens.global.auth.CustomUserDetails;
import java.util.List;
import java.util.Map;
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

    private final AgoraMemberService agoraMemberService;

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        logConnectEvent(event);
    }

    private void logConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        if (accessor.getUser() == null) {
            return;
        }
        Authentication authentication = (Authentication) accessor.getUser();
        String memberName = authentication.getName();
        String memberRole = getMemberRole(authentication);

        log.info("WebSocket {}: memberName={}, memberRole={}", event.getClass().getSimpleName(), memberName,
                memberRole);
    }

    private String getMemberRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalArgumentException("Member role is not exist."));
    }

    @EventListener
    public void handleWebSocketSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
        Map nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");

        if (accessor.getUser() != null) {
            Long userId = getUserId(accessor);
            if (nativeHeaders.containsKey("AgoraId")) {
                Long agoraId = getAgoraId(nativeHeaders);
                String sessionId = (String) generic.getHeaders().get("simpSessionId");

                agoraMemberService.updateSessionId(agoraId, userId, sessionId);
                agoraMemberService.sendMetaToActiveMembers(agoraId);
                log.info("SessionId updated: agoraId={}, userId={}, sessionId={}", agoraId, userId, sessionId);
            } else {
                log.warn("AgoraId is not exist in headers");
            }
        } else {
            log.warn("User is not exist in headers");
        }
        log.info("WebSocket Connected");
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
        Long agoraId = agoraMemberService.findAgoraIdBySessionId(sessionId);
        Long userId = getUserId(StompHeaderAccessor.wrap(event.getMessage()));

        agoraMemberService.removeSessionId(sessionId);
        agoraMemberService.sendMetaToActiveMembers(agoraId);

        log.info("WebSocket Disconnected: sessionId={}, agoraId={}, userId={}", sessionId, agoraId, userId);
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
