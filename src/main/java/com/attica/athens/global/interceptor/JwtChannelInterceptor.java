package com.attica.athens.global.interceptor;

import com.attica.athens.global.security.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_ = "Bearer ";

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            checkUserIdAndRoleInSessionAttributes(accessor);
            return message;
        }

        String jwtToken = accessor.getFirstNativeHeader(AUTHORIZATION);

        if (jwtToken == null || !jwtToken.startsWith(BEARER_)) {
            throw new RuntimeException("Invalid token");
        }

        jwtToken = jwtToken.substring(BEARER_.length());

        if (jwtUtil.isExpired(jwtToken)) {
            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        String userId = jwtUtil.getId(jwtToken);
        String userRole = jwtUtil.getRole(jwtToken);

        accessor.getSessionAttributes().put("userId", userId);
        accessor.getSessionAttributes().put("userRole", userRole);

        return message;
    }

    private void checkUserIdAndRoleInSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null ||
                !sessionAttributes.containsKey("userId") ||
                !sessionAttributes.containsKey("userRole")) {
            throw new IllegalStateException("Session attributes : userId or userRole is missing");
        }
    }
}
