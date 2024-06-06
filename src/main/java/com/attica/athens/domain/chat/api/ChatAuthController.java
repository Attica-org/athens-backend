package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.application.ChatCommandService;
import com.attica.athens.domain.chat.application.ChatQueryService;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.GetChatParticipants;
import com.attica.athens.domain.chat.dto.response.GetChatResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    @MessageMapping("/agoras/{agoraId}/chats")
    @SendTo(value = "/topic/agoras/{agoraId}/chats")
    public SendChatResponse sendChat(@DestinationVariable("agoraId") Long agoraId,
                                     @Payload @Valid SendChatRequest sendChatRequest,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chatCommandService.sendChat(userDetails, agoraId, sendChatRequest);
    }

    @MessageMapping("/agoras/{agoraId}")
    @SendTo(value = "/topic/agoras/{agoraId}")
    public SendMetaResponse sendMeta(@DestinationVariable("agoraId") Long agoraId) {

        return chatQueryService.sendMeta(agoraId);
    }

    @GetMapping("/agoras/{agoraId}/chats")
    public ResponseEntity getChatHistory(@PathVariable("agoraId") Long agoraId,
                                         @RequestBody Cursor cursor) {

        GetChatResponse response = chatQueryService.getChatHistory(agoraId, cursor);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/agoras/{agoraId}/users")
    public ResponseEntity getChatParticipants(@PathVariable("agoraId") Long agoraId) {

        GetChatParticipants response = chatQueryService.getChatParticipants(agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
