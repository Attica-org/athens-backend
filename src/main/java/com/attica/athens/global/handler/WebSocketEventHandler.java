package com.attica.athens.global.handler;

import com.attica.athens.domain.agoraMember.application.AgoraMemberService;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.global.auth.exception.InvalidAuthorizationHeaderException;
import com.attica.athens.global.decorator.HeartBeatManager;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
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

    private static final String AGORA_ID_HEADER = "AgoraId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOPIC_PREFIX = "/topic/agoras/";
    private static final Integer AGORA_ID_INDEX = 3;

    private final HeartBeatManager heartBeatManager;
    private final AgoraMemberService agoraMemberService;
    private final AuthService authService;

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();
        if (authentication == null) {
            log.warn("Unauthenticated connection attempt");
            return;
        }
        String memberName = authentication.getName();
        String memberRole = getMemberRole(authentication);

        log.info("WebSocket Connect: memberName={}, memberRole={}", memberName, memberRole);
    }

    @EventListener
    public void handleWebSocketSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Long memberId = getUserId(accessor);
        Map<String, List<String>> nativeHeaders = getNativeHeaders(accessor);

        if (!validateHeaders(nativeHeaders, memberId)) {
            return;
        }

        Long agoraId = getAgoraId(nativeHeaders);
        String sessionId = getSessionId(accessor);
        String accessToken = extractAccessToken(nativeHeaders);

        authService.verifyToken(accessToken);

        AgoraMember agoraMember = agoraMemberService.findAgoraMemberByAgoraIdAndMemberId(agoraId, memberId);
        handleConnectionLogic(agoraMember, agoraId, memberId, sessionId);
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Long agoraId = agoraMemberService.findAgoraIdBySessionId(sessionId);
        Long memberId = getUserId(StompHeaderAccessor.wrap(event.getMessage()));

        AgoraMember agoraMember = agoraMemberService.findAgoraMemberByAgoraIdAndMemberId(agoraId, memberId);
        if (agoraMember.getDisconnectType()) {
            processDisconnection(sessionId, agoraId, memberId);
        } else {
            log.warn("Temporary disconnection: sessionId={}, agoraId={}, memberId={}", sessionId, agoraId, memberId);
        }
    }

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebSocketSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith(TOPIC_PREFIX)) {
            Long agoraId = extractAgoraIdFromDestination(destination);
            Long userId = getUserId(headerAccessor);

            heartBeatManager.handleHeartbeat(sessionId);
            agoraMemberService.sendMetaToActiveMembers(agoraId, userId);
        }
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void handleWebSocketSessionUnsubscribe(SessionUnsubscribeEvent event) {
        log.info("WebSocket Unsubscribe");
    }

    private void handleConnectionLogic(AgoraMember agoraMember, Long agoraId, Long memberId, String sessionId) {
        if (agoraMember.getSessionId() == null) {
            handleNewConnection(agoraId, memberId, sessionId);
        } else if (!agoraMember.getSessionId().equals(sessionId)) {
            handleExistingConnection(agoraMember, agoraId, memberId, sessionId);
        }
    }

    private void handleNewConnection(Long agoraId, Long userId, String sessionId) {
        log.info("Starting new connection: agoraId={}, userId={}, sessionId={}", agoraId, userId, sessionId);
        try {
            agoraMemberService.updateSessionId(agoraId, userId, sessionId);
        } catch (Exception e) {
            log.error("Error in new connection: agoraId={}, userId={}, sessionId={}, error={}", agoraId, userId,
                    sessionId, e.getMessage(), e);
            throw e;
        }
        log.info("New connection completed: agoraId={}, userId={}, sessionId={}", agoraId, userId, sessionId);
    }

    private void handleExistingConnection(AgoraMember agoraMember, Long agoraId, Long memberId,
                                          String sessionId) {
        if (heartBeatManager.isReconnectValid(agoraMember.getSessionId())) {
            heartBeatManager.removeSession(agoraMember.getSessionId());
            reconnectAgoraMember(agoraMember, agoraId, memberId, sessionId);
        } else {
            processDisconnection(agoraMember.getSessionId(), agoraId, memberId);
            heartBeatManager.removeSession(agoraMember.getSessionId());
        }
    }

    @Transactional
    public void reconnectAgoraMember(AgoraMember agoraMember, Long agoraId, Long memberId, String sessionId) {
        agoraMember.updateDisconnectType(false);
        handleNewConnection(agoraId, memberId, sessionId);
        heartBeatManager.handleHeartbeat(sessionId);
    }

    @Transactional
    public void processDisconnection(String sessionId, Long agoraId, Long memberId) {
        try {
            agoraMemberService.removeSessionId(sessionId);
            agoraMemberService.sendMetaToActiveMembers(agoraId, memberId);
            log.info("WebSocket Disconnected: agoraId={}, userId={}", agoraId, memberId);
        } catch (Exception e) {
            log.error("Error during disconnection: agoraId={}, userId={}, error={}", agoraId, memberId, e.getMessage(),
                    e);
            throw e;
        }
    }

    private String getMemberRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalArgumentException("Member role not found."));
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    private Map<String, List<String>> getNativeHeaders(StompHeaderAccessor accessor) {
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        return (Map<String, List<String>>) generic.getHeaders().get("nativeHeaders");
    }

    private String getSessionId(StompHeaderAccessor accessor) {
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        return (String) generic.getHeaders().get("simpSessionId");
    }

    private boolean validateHeaders(Map<String, List<String>> nativeHeaders, Long memberId) {
        if (memberId == null) {
            log.warn("User is not present in headers");
            return false;
        }

        if (!nativeHeaders.containsKey(AGORA_ID_HEADER)) {
            log.warn("AgoraId is not present in headers");
            return false;
        }
        return true;
    }

    private Long getAgoraId(Map<String, List<String>> nativeHeaders) {
        return Long.parseLong(nativeHeaders.get(AGORA_ID_HEADER).get(0));
    }

    private String extractAccessToken(Map<String, List<String>> nativeHeaders) {
        List<String> authHeaders = nativeHeaders.get(AUTHORIZATION_HEADER);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            return authHeader.startsWith(BEARER_PREFIX) ? authHeader.substring(BEARER_PREFIX.length()) : authHeader;
        }
        throw new InvalidAuthorizationHeaderException();
    }

    private Long extractAgoraIdFromDestination(String destination) {
        String[] parts = destination.split("/");
        return Long.parseLong(parts[AGORA_ID_INDEX]);
    }
}
