package com.attica.athens.domain.chat.dto.request;

import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.domain.chat.domain.ReactionType;
import jakarta.validation.constraints.NotNull;

public record SendReactionRequest(
        @NotNull(message = "Chat type cannot be null") ChatType type,
        @NotNull(message = "Reaction type cannot be null") ReactionType reactionType
) {
}
