package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;

public class TestDoubleFactory {

    public static AgoraUser createBasicAgoraUser() {
        return AgoraUser.builder()
                .type(AgoraUserType.PROS)
                .build();
    }

    public static ChatContent createBasicChatContent() {
        return new ChatContent("안녕");
    }

    public static Chat createBasicChat() {
        return new Chat(ChatType.CHAT, createBasicChatContent(), createBasicAgoraUser());
    }
}
