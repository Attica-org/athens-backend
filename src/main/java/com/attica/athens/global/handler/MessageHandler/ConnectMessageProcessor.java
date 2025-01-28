package com.attica.athens.global.handler.MessageHandler;

import static com.attica.athens.global.auth.jwt.Constants.AUTHORIZATION;
import static com.attica.athens.global.auth.jwt.Constants.BEARER_;

import com.attica.athens.global.auth.application.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;

@Slf4j
@RequiredArgsConstructor
public class ConnectMessageProcessor implements MessageProcessor{
    private final Message<?> message;
    private final StompHeaderAccessor accessor;
    private final AuthService authService;

    @Override
    public Message<?> process() {
        String sessionId = accessor.getSessionId();
        String jwtToken = extractJwtToken(accessor);

        if (jwtToken != null) {
            Authentication authentication = authService.createAuthenticationByToken(jwtToken);
            accessor.setUser(authentication);
            log.info("Authentication set for session: {}", sessionId);
        }

        return message;
    }

    private String extractJwtToken(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION))
                .filter(token -> token.startsWith(BEARER_))
                .map(token -> token.substring(BEARER_.length()))
                .filter(authService::verifyToken)
                .orElse(null);
    }
}
