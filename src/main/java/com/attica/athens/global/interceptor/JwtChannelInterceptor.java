package com.attica.athens.global.interceptor;

import static com.attica.athens.global.auth.jwt.Constants.AUTHORIZATION;
import static com.attica.athens.global.auth.jwt.Constants.BEARER_;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.decorator.HeartBeatManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final AuthService authService;
    private final HeartBeatManager heartBeatManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            log.warn("StompHeaderAccessor is null");
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnectCommand(accessor);
        } else if (isHeartbeat(accessor, message)) {
            handleHeartbeat(accessor);
        }

        return message;
    }

    private void handleConnectCommand(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        log.debug("CONNECT command received. SessionId: {}", sessionId);

        String jwtToken = extractJwtToken(accessor);
        if (jwtToken == null) {
            log.warn("JWT token is null for session: {}", sessionId);
            return;
        }

        Authentication authentication = authService.createAuthenticationByToken(jwtToken);
        accessor.setUser(authentication);
        log.info("Authentication set for session: {}", sessionId);
    }

    private boolean isHeartbeat(StompHeaderAccessor accessor, Message<?> message) {
        return accessor.getCommand() == null &&
                message.getPayload() instanceof byte[] &&
                ((byte[]) message.getPayload()).length == 0;
    }

    private void handleHeartbeat(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        log.debug("Heartbeat received. SessionId: {}", sessionId);
        heartBeatManager.handleHeartbeat(sessionId);
    }

    private String extractJwtToken(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION))
                .filter(token -> token.startsWith(BEARER_))
                .map(token -> token.substring(BEARER_.length()))
                .filter(authService::validateToken)
                .orElse(null);
    }
}