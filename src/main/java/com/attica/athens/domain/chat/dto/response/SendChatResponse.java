package com.attica.athens.domain.chat.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatType;

public record SendChatResponse(ChatType type, SendChatData data) {

    public SendChatResponse(AgoraMember agoraMember, Chat chat) {
        this(chat.getType(), new SendChatData(chat, agoraMember));
    }

    public record SendChatData(
            Long chatId,
            UserData user,
            String content,
            String createdAt
    ) {
        public SendChatData(Chat chat, AgoraMember agoraMember) {
            this(chat.getId(), new UserData(agoraMember), chat.getContent().getContent(), format(chat.getCreatedAt()));
        }
    }

    public record UserData(Long id, String nickname, Integer photoNumber, AgoraMemberType type) {

        public UserData(AgoraMember agoraMember) {
            this(agoraMember.getId(), agoraMember.getNickname(), agoraMember.getPhotoNumber(),
                    agoraMember.getType());
        }
    }
}


