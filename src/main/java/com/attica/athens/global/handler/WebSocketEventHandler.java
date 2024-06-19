package com.attica.athens.global.handler;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.exception.NotFoundActiveAgoraUserException;
import com.attica.athens.domain.agoraUser.exception.NotFoundAgoraUserException;
import com.attica.athens.domain.agoraUser.exception.findActiveAgorasException;
import com.attica.athens.global.auth.CustomUserDetails;
import java.security.Principal;
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
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
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

    private void getUserDetailsAndSessionId(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);

        CustomUserDetails customUserDetails = getUserDetails(
                Objects.requireNonNull(accessor.getUser(), "User cannot be null"));
        String sessionId = accessor.getSessionId();

        updateSessionId(customUserDetails.getUserId(), sessionId);
    }

    private <T extends AbstractSubProtocolEvent> CustomUserDetails getUserDetails(Principal principle) {
        Authentication authentication = (Authentication) Objects.requireNonNull(principle);
        return (CustomUserDetails) authentication.getPrincipal();
    }

    @Transactional
    public void updateSessionId(Long userId, String sessionId) {

        AgoraUser agoraUser = getAgoraUser(userId);

        agoraUser.updateSessionId(sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);

        CustomUserDetails userDetails = getUserDetails(accessor.getUser());
        AgoraUser agoraUser = updateIsDelete(userDetails);

        Agora agora = agoraUser.getAgora();
        checkAgoraIsDelete(agora);
    }

    private void checkAgoraIsDelete(Agora agora) {
        List<AgoraUser> agoraUsers = agoraUserRepository.findByAgoraIdAndAgoraStatus(agora.getId(),
                AgoraStatus.RUNNING);

        if (agoraUsers.isEmpty()) {
            agora.timeOutAgora();
        }
    }

    @Transactional
    public AgoraUser updateIsDelete(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        AgoraUser agoraUser = getAgoraUser(userId);
        agoraUser.delete();

        return agoraUser;
    }

    private AgoraUser getAgoraUser(Long userId) {
        List<AgoraUser> agoraUsers = agoraUserRepository.findByUserId(userId);

        if (agoraUsers.isEmpty()) {
            throw new NotFoundAgoraUserException(userId);
        }

        List<AgoraUser> allAgoraUser = findActiveAgoraUser(agoraUsers);
        if (allAgoraUser.size() > 1) {
            throw new findActiveAgorasException();
        }
        if (allAgoraUser.isEmpty()) {
            throw new NotFoundActiveAgoraUserException();
        }

        return allAgoraUser.get(0);
    }

    private List<AgoraUser> findActiveAgoraUser(List<AgoraUser> agoraUsers) {
        return agoraUsers.stream()
                .filter(agoraUser -> agoraUser.getAgora().getStatus() != AgoraStatus.CLOSED)
                .toList();
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
}
