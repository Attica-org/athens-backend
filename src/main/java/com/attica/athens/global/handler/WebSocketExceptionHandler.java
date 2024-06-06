package com.attica.athens.global.handler;

import com.attica.athens.domain.chat.dto.response.ErrorResponse;
import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.global.decorator.CustomWebSocketHandlerDecorator;
import java.io.IOException;
import java.net.SocketException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final CustomWebSocketHandlerDecorator decorator;

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleSocketException(SocketException exception, Message<?> message) throws IOException {
        removeSession(message);

        return new ErrorResponse(3000, "SOCKET_ERROR");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleCustomException(CustomException exception, Message<?> message)
            throws IOException {
        removeSession(message);

        return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleIllegalArgumentException(RuntimeException exception, Message<?> message)
            throws IOException {
        removeSession(message);

        return new ErrorResponse(2000, "Runtime Exception");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, Message<?> message)
            throws IOException {
        removeSession(message);

        return new ErrorResponse(
                1001,
                ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "))
        );
    }

    private void removeSession(Message<?> message) throws IOException {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        String sessionId = stompHeaderAccessor.getSessionId();
        log.info("session = {}, connection remove", sessionId);
        decorator.closeSession(sessionId);
    }
}
