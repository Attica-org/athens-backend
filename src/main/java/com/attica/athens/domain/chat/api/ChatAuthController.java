package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.application.ChatCommandService;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.request.SendReactionRequest;
import com.attica.athens.domain.chat.dto.response.BadWordResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendReactionResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @SubscribeMapping(value = "/topic/agoras/{agoraId}/kick")
    public SendReactionResponse sendKick(
            @DestinationVariable("agoraId") Long agoraId,
            @DestinationVariable("chatId") Long chatId,
            @Payload @Valid SendReactionRequest sendReactionRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return chatCommandService.sendReaction(userDetails, agoraId, chatId, sendReactionRequest);
    }

    @PostMapping("/agoras/{agoraId}/chats/filter")
    public ResponseEntity<ApiResponse<BadWordResponse>> filterChat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("agoraId") Long agoraId,
            @RequestBody SendChatRequest sendChatRequest) {

        BadWordResponse response = chatCommandService.checkBadWord(userDetails, agoraId, sendChatRequest);

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
