package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.application.ChatCommandService;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.global.utils.WebSocketUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatCommandService chatCommandService;

    @MessageMapping("/agoras/{agora-id}/chats")
    @SendTo(value = "/topic/agoras/{agora-id}/chats")
    public SendChatResponse sendChat(@DestinationVariable("agora-id") Long agoraId,
                                     @Payload SendChatRequest sendChatRequest,
                                     SimpMessageHeaderAccessor accessor) {

        String userId = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
        String userRole = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("userRole");

        WebSocketUtils.setSessionAttributes(accessor.getSessionAttributes());

        SendChatResponse response = chatCommandService.sendChat(userId, userRole, agoraId, sendChatRequest);

        WebSocketUtils.removeSessionAttributes();

        return response;

    }
}
