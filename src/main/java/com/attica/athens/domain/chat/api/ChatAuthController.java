package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.application.ChatCommandService;
import com.attica.athens.domain.chat.application.ChatQueryService;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.ErrorResponse;
import com.attica.athens.domain.chat.dto.response.GetChatParticipants;
import com.attica.athens.domain.chat.dto.response.GetChatResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import java.net.SocketException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ChatAuthController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    @MessageMapping("/agoras/{agora-id}/chats")
    @SendTo(value = "/topic/agoras/{agora-id}/chats")
    public SendChatResponse sendChat(@DestinationVariable("agora-id") Long agoraId,
                                     @Payload @Valid SendChatRequest sendChatRequest,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chatCommandService.sendChat(userDetails, agoraId, sendChatRequest);
    }

    @MessageMapping("/agoras/{agora-id}")
    @SendTo(value = "/topic/agoras/{agora-id}")
    public SendMetaResponse sendMeta(@DestinationVariable("agora-id") Long agoraId) {

        return chatQueryService.sendMeta(agoraId);
    }

    @GetMapping("/agoras/{agora-id}/chats")
    public ResponseEntity getChatHistory(@PathVariable("agora-id") Long agoraId,
                                         @RequestBody Cursor cursor) {

        GetChatResponse response = chatQueryService.getChatHistory(agoraId, cursor);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/agoras/{agora-id}/users")
    public ResponseEntity getChatParticipants(@PathVariable("agora-id") Long agoraId) {

        GetChatParticipants response = chatQueryService.getChatParticipants(agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleException(SocketException exception) {
        log.warn(exception.getMessage());

        return ErrorResponse.from(exception.getMessage(), "SOCKET_ERROR");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(exception.getMessage());

        return ErrorResponse.from("ILLEGAL_ARGUMENT", exception.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleValidationE현xception(MethodArgumentNotValidException ex) {

        return ErrorResponse.from(
                "VALIDATION_ERROR",
                ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "))
        );
    }
}
