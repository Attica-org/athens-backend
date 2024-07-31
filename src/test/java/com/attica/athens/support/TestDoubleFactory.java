package com.attica.athens.support;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatContent;
import com.attica.athens.domain.chat.domain.ChatType;

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
