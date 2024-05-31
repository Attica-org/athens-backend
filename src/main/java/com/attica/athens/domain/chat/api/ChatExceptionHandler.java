package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.dto.response.ErrorResponse;
import java.net.SocketException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class ChatExceptionHandler {

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleSocketException(SocketException exception) {
        log.warn(exception.getMessage());

        return new ErrorResponse(exception.getMessage(), "SOCKET_ERROR");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(exception.getMessage());

        return new ErrorResponse("ILLEGAL_ARGUMENT", exception.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleValidationE현xception(MethodArgumentNotValidException ex) {

        return new ErrorResponse(
                "VALIDATION_ERROR",
                ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "))
        );
    }
}
