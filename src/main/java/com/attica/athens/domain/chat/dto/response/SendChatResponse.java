package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatType;
import java.time.LocalDateTime;

public record SendChatResponse(ChatType type, SendChatData data) {

    public record SendChatData(
            Long chatId,
            Long agoraId,
            UserData user,
            String content,
            LocalDateTime createdAt
    ) {
        public static SendChatData createSendChatData(Chat chat, Agora agora, AgoraUser agoraUser) {
            return new SendChatData(chat.getId(), agora.getId(), UserData.from(agoraUser), chat.getContent(),
                    chat.getCreatedAt());
        }
    }

    public record UserData(Long id, String nickname, Integer photoNumber, AgoraUserType type) {

        public static UserData from(AgoraUser agoraUser) {
            return new UserData(agoraUser.getId(), agoraUser.getNickname(), agoraUser.getPhotoNumber(),
                    agoraUser.getType());
        }
    }
}


