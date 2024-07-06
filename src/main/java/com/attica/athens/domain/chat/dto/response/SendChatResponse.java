package com.attica.athens.domain.chat.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatType;

public record SendChatResponse(ChatType type, SendChatData data) {

    public SendChatResponse(AgoraUser agoraUser, Chat chat) {
        this(chat.getType(), new SendChatData(chat, agoraUser));
    }

    public record SendChatData(
            Long chatId,
            UserData user,
            String content,
            String createdAt
    ) {
        public SendChatData(Chat chat, AgoraUser agoraUser) {
            this(chat.getId(), new UserData(agoraUser), chat.getContent(), format(chat.getCreatedAt()));
        }
    }

    public record UserData(Long id, String nickname, Integer photoNumber, AgoraUserType type) {

        public UserData(AgoraUser agoraUser) {
            this(agoraUser.getId(), agoraUser.getNickname(), agoraUser.getPhotoNumber(),
                    agoraUser.getType());
        }
    }
}


