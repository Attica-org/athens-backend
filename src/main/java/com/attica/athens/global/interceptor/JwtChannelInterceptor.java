package com.attica.athens.global.interceptor;

import static com.attica.athens.global.auth.jwt.Constants.AUTHORIZATION;
import static com.attica.athens.global.auth.jwt.Constants.BEARER_;

import com.attica.athens.global.auth.application.AuthService;
import java.util.Optional;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final AuthService authService;

    public JwtChannelInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        Optional<String> jwtTokenOptional = Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION));
        String jwtToken = jwtTokenOptional
                .filter(token -> token.startsWith(BEARER_))
                .map(token -> token.substring(BEARER_.length()))
                .filter(token -> !authService.validateToken(token))
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        Authentication authentication = authService.createAuthenticationByToken(jwtToken);
        accessor.setUser(authentication);

        return message;
    }
}