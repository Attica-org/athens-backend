package com.attica.athens.global.interceptor;

import static com.attica.athens.global.auth.jwt.Constants.AUTHORIZATION;
import static com.attica.athens.global.auth.jwt.Constants.BEARER_;

import com.attica.athens.global.auth.application.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final AuthService authService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = extractJwtToken(accessor);
            if (jwtToken == null) {
                return message;
            }

            Authentication authentication = authService.createAuthenticationByToken(jwtToken);
            accessor.setUser(authentication);
        }

        return message;
    }

    private String extractJwtToken(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION))
                .filter(token -> token.startsWith(BEARER_))
                .map(token -> token.substring(BEARER_.length()))
                .filter(authService::validateToken)
                .orElse(null);
    }
}
