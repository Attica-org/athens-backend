package com.attica.athens.domain.chat.dto.request;

import com.attica.athens.domain.chat.domain.ChatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendChatRequest(
        @NotNull(message = "Chat type cannot be null") ChatType type,
        @NotBlank(message = "Chat message cannot be empty") String message
) {
}
