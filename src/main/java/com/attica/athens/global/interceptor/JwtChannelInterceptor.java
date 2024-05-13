package com.attica.athens.global.interceptor;

import com.attica.athens.global.security.CustomUserDetailsService;
import com.attica.athens.global.security.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_ = "Bearer ";
    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String jwtToken = accessor.getFirstNativeHeader(AUTHORIZATION);

        if (jwtToken == null || !jwtToken.startsWith(BEARER_)) {
            throw new RuntimeException("Invalid token");
        }

        jwtToken = jwtToken.substring(7);
        try {
            if (jwtUtil.isExpired(jwtToken)) {
                throw new ExpiredJwtException(null, null, "Token has expired");
            }
            String userId = jwtUtil.getId(jwtToken);
            String userRole = jwtUtil.getRole(jwtToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userRole);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token has expired");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred");
        }
        return message;
    }
}
