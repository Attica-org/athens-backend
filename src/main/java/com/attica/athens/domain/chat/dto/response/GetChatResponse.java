package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.UserData;
import java.time.LocalDateTime;
import java.util.List;

public record GetChatResponse(List<ChatData> chats, Cursor meta) {
    public record ChatData(
            Long chatId,
            UserData user,
            String content,
            LocalDateTime createdAt
    ) {
        public ChatData(Chat chat, AgoraUser agoraUser) {
            this(chat.getId(), new UserData(agoraUser), chat.getContent(),
                    chat.getCreatedAt());
        }
    }
}
