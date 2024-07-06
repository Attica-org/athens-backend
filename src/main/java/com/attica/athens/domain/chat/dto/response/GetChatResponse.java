package com.attica.athens.domain.chat.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.UserData;
import java.util.List;

public record GetChatResponse(List<ChatData> chats, Cursor meta) {
    public record ChatData(
            Long chatId,
            UserData user,
            String content,
            String createdAt
    ) {
        public ChatData(Chat chat, AgoraUser agoraUser) {
            this(chat.getId(), new UserData(agoraUser), chat.getContent().getContent(),
                    format(chat.getCreatedAt()));
        }
    }
}
