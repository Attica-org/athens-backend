package com.attica.athens.global.handler;

import com.attica.athens.domain.chat.dto.response.ErrorResponse;
import com.attica.athens.global.auth.exception.InvalidAuthorizationHeaderException;
import com.attica.athens.global.auth.exception.JwtExpiredException;
import com.attica.athens.global.auth.exception.JwtIllegalArgumentException;
import com.attica.athens.global.auth.exception.JwtSignatureException;
import com.attica.athens.global.auth.exception.JwtUnsupportedJwtException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if (ex instanceof MessageDeliveryException) {
            Throwable cause = ex.getCause();
            if (cause instanceof AccessDeniedException) {
                return sendErrorMessage(new ErrorResponse(1201, "Access denied"));
            }

            if (cause instanceof InvalidAuthorizationHeaderException) {
                return sendErrorMessage(new ErrorResponse(1003, cause.getMessage()));
            }

            if (isJwtException(cause)) {
                return sendErrorMessage(new ErrorResponse(1201, cause.getMessage()));
            }
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private boolean isJwtException(Throwable ex) {
        return ex instanceof JwtSignatureException
                || ex instanceof JwtExpiredException
                || ex instanceof JwtUnsupportedJwtException
                || ex instanceof JwtIllegalArgumentException;
    }

    private Message<byte[]> sendErrorMessage(ErrorResponse errorResponse) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
        headers.setMessage(errorResponse.message());
        headers.setLeaveMutable(true);

        try {
            String json = objectMapper.writeValueAsString(errorResponse);
            return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8),
                    headers.getMessageHeaders());
        } catch (JsonProcessingException e) {
            log.error("Failed to convert ErrorResponse to JSON", e);
            return MessageBuilder.createMessage(errorResponse.message().getBytes(StandardCharsets.UTF_8),
                    headers.getMessageHeaders());
        }
    }
}
