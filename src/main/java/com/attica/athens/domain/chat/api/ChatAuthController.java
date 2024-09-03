package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.application.ChatCommandService;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.request.SendReactionRequest;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendReactionResponse;
import com.attica.athens.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ChatAuthController {

    private final ChatCommandService chatCommandService;

    @MessageMapping("/agoras/{agoraId}/chats")
    @SendTo(value = "/topic/agoras/{agoraId}/chats")
    public SendChatResponse sendChat(
            @DestinationVariable("agoraId") Long agoraId,
            @Payload @Valid SendChatRequest sendChatRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chatCommandService.sendChat(userDetails, agoraId, sendChatRequest);
    }

    @MessageMapping("/agoras/{agoraId}/chats/{chatId}/reactions")
    @SendTo(value = "/topic/agoras/{agoraId}/reactions")
    public SendReactionResponse sendReaction(
            @DestinationVariable("agoraId") Long agoraId,
            @DestinationVariable("chatId") Long chatId,
            @Payload @Valid SendReactionRequest sendReactionRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return chatCommandService.sendReaction(userDetails, agoraId, chatId, sendReactionRequest);
    }
}
