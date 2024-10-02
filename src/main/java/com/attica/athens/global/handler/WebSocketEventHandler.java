package com.attica.athens.global.handler;

import com.attica.athens.domain.agoraMember.application.AgoraMemberService;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.member.exception.NotFoundMemberException;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.global.auth.exception.InvalidAuthorizationHeaderException;
import com.attica.athens.global.auth.exception.SessionReconnectException;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private final AgoraMemberRepository agoraMemberRepository;
    private final AuthService authService;
    private final long reconnectThreshold = 10000;

    @EventListener
    public void handleWebSocketSessionConnect(SessionConnectEvent event) {
        logConnectEvent(event);
    }

    private void logConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        if (accessor.getUser() == null) {
            log.warn("Unauthenticated connection attempt");
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

        Long memberId = getUserId(accessor);
        if (!validateHeaders(nativeHeaders, memberId)) {
            return;
        }

        Long agoraId = getAgoraId(nativeHeaders);
        String sessionId = (String) generic.getHeaders().get("simpSessionId");

        AgoraMember agoraMember = getAgoraMember(memberId);  // 중복데이터 때문에 수정할 필요 있음

        String accessToken = extractAccessToken(nativeHeaders);
        authService.validateToken(accessToken);

        if (agoraMember.getSessionId() == null) {
            handleNewConnection(agoraId, memberId, sessionId);
        } else if (agoraMember.getSocketDisconnectTime() != null) {
            findDisconnectTime(agoraMember);
            handleReconnection(agoraMember);
        }

        log.info("WebSocket Connected: sessionId={}, agoraId={}, userId={}", sessionId, agoraId, memberId);
    }

    private static boolean validateHeaders(Map nativeHeaders, Long memberId) {
        if (memberId == null) {
            log.warn("User is not present in headers");
            return false;
        }

        if (!nativeHeaders.containsKey("AgoraId")) {
            log.warn("AgoraId is not present in headers");
            return false;
        }
        return true;
    }

    private void handleReconnection(AgoraMember agoraMember) {
        boolean isReconnected = checkDisconnectTime(agoraMember.getSocketDisconnectTime());
        if (isReconnected) {
            log.info("Reconnected within threshold: sessionId={}, agoraId={}, userId={}", agoraMember.getSessionId(),
                    agoraMember.getAgora().getId(), agoraMember.getMember().getId());
        } else {
            log.info("Session reconnection failed: sessionId={}, agoraId={}, userId={}", agoraMember.getSessionId(),
                    agoraMember.getAgora().getId(), agoraMember.getMember().getId());
            //processDisconnection(agoraMember.getSessionId(), agoraMember.getAgora().getId(), agoraMember.getId());
            // 이 부분 예외 주면 프론트에서 eixt api 호출하는 것으로 해야할듯
            throw new SessionReconnectException(agoraMember.getSessionId());
        }
    }

    private void handleNewConnection(Long agoraId, Long userId, String sessionId) {
        log.info("Starting handleNewConnection: agoraId={}, userId={}, sessionId={}", agoraId, userId, sessionId);
        try {
            agoraMemberService.updateSessionId(agoraId, userId, sessionId);
        } catch (Exception e) {
            log.error("Error in handleNewConnection: agoraId={}, userId={}, sessionId={}, error={}", agoraId, userId,
                    sessionId, e.getMessage(), e);
            throw e;
        }
        log.info("handleNewConnection completed successfully: agoraId={}, userId={}, sessionId={}", agoraId, userId,
                sessionId);
    }

    private boolean checkDisconnectTime(LocalDateTime disconnectTime) {
        if (Duration.between(LocalDateTime.now(), disconnectTime).getSeconds() < reconnectThreshold) {
            return true;
        }
        return false;
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    private long getAgoraId(Map nativeHeaders) {
        return Long.parseLong((String) ((List<?>) nativeHeaders.get("AgoraId")).get(0));
    }

    private String extractAccessToken(Map nativeHeaders) {
        List<String> authHeaders = (List<String>) nativeHeaders.get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        }
        throw new InvalidAuthorizationHeaderException();
    }

    @EventListener
    public void handleWebSocketSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Long agoraId = agoraMemberService.findAgoraIdBySessionId(sessionId);
        Long memberId = getUserId(StompHeaderAccessor.wrap(event.getMessage()));

        AgoraMember agoraMember = getAgoraMember(memberId);  // agoraId, memberId 같이 조회하자
        if (agoraMember.getDisconnectType()) {
            processDisconnection(sessionId, agoraId, memberId);
        } else {
            agoraMember.updateSocketDisconnectTime(LocalDateTime.now());
            AgoraMember updateAgoraMember = getAgoraMember(memberId);
            agoraMemberRepository.save(updateAgoraMember);
            log.warn("Disconnection type check failed: agoraId={}, memberId={}", agoraId, memberId);
        }
    }

    private void processDisconnection(String sessionId, Long agoraId, Long memberId) {
        agoraMemberService.removeSessionId(sessionId);
        agoraMemberService.sendMetaToActiveMembers(agoraId, memberId);
        log.info("WebSocket Disconnected: agoraId={}, userId={}", agoraId, memberId);
        agoraMemberService.deleteAgoraMember(agoraId, memberId);
    }

    private AgoraMember getAgoraMember(Long userId) {
        return agoraMemberRepository.findByMemberId(userId)
                .orElseThrow(() -> new NotFoundMemberException(userId));
    }

    private LocalDateTime findDisconnectTime(AgoraMember paramAgoraMember) {
        AgoraMember agoraMember = agoraMemberRepository.findBySessionId(paramAgoraMember.getSessionId())
                .orElseThrow(() -> new NotFoundMemberException(paramAgoraMember.getMember().getId()));

        return agoraMember.getSocketDisconnectTime();
    }

    @EventListener(SessionSubscribeEvent.class)
    public void handleWebSocketSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith("/topic/agoras/")) {
            Long agoraId = extractAgoraIdFromDestination(destination);
            Long userId = getUserId(headerAccessor);

            agoraMemberService.sendMetaToActiveMembers(agoraId, userId);
        }
    }

    private Long extractAgoraIdFromDestination(String destination) {
        String[] parts = destination.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    @EventListener(SessionUnsubscribeEvent.class)
    public void handleWebSocketSessionUnsubscribe() {
        log.info("WebSocket Unsubscribe");
    }
}
