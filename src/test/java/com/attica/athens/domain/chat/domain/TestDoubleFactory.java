package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;

public class TestDoubleFactory {

    public static AgoraMember createBasicAgoraMember() {
        return AgoraMember.builder()
                .type(AgoraMemberType.PROS)
                .build();
    }

    public static ChatContent createBasicChatContent() {
        return new ChatContent("안녕");
    }

    public static Chat createBasicChat() {
        return new Chat(ChatType.CHAT, createBasicChatContent(), createBasicAgoraMember());
    }
}
