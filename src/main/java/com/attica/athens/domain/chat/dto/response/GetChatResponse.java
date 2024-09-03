package com.attica.athens.domain.chat.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.UserData;
import java.util.List;
import java.util.Map;

public record GetChatResponse(List<ChatData> chats, Cursor meta) {
    public record ChatData(
            Long chatId,
            UserData user,
            String content,
            String createdAt,
            Map<ReactionType, Long> reactionCount
    ) {
        public ChatData(Chat chat, AgoraMember agoraMember, Map<ReactionType, Long> reactionCount) {
            this(chat.getId(), new UserData(agoraMember), chat.getContent().getContent(),
                    format(chat.getCreatedAt()), reactionCount);
        }
    }
}
