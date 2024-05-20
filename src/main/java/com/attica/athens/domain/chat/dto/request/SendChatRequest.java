package com.attica.athens.domain.chat.dto.request;

import com.attica.athens.domain.chat.domain.ChatType;

public record SendChatRequest(
        ChatType type,
        String message
) {
}
