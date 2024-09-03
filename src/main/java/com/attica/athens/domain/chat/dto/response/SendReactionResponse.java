package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.domain.chat.domain.ReactionType;
import java.util.Map;

public record SendReactionResponse(ChatType type, SendReactionData data) {

    public SendReactionResponse(Long chatId, Map<ReactionType, Long> reactionCount) {
        this(ChatType.REACTION, new SendReactionData(chatId, reactionCount));
    }

    public record SendReactionData(
            Long chatId,
            Map<ReactionType, Long> reactionCount
    ) {
    }
}
