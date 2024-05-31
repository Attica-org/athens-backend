package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatType;
import java.time.LocalDateTime;

public record SendChatResponse(ChatType type, SendChatData data) {

    public record SendChatData(
            Long chatId,
            UserData user,
            String content,
            LocalDateTime createdAt
    ) {
        public SendChatData(Chat chat, AgoraUser agoraUser) {
            this(chat.getId(), new UserData(agoraUser), chat.getContent(),
                    chat.getCreatedAt());
        }
    }

    public record UserData(Long id, String nickname, Integer photoNumber, AgoraUserType type) {

        public UserData(AgoraUser agoraUser) {
            this(agoraUser.getId(), agoraUser.getNickname(), agoraUser.getPhotoNumber(),
                    agoraUser.getType());
        }
    }
}


