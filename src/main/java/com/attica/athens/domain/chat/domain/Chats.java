package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.dto.response.GetChatResponse.ChatData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Chats {

    private final List<Chat> chats;

    public Chats(List<Chat> chats) {
        this.chats = new ArrayList<>(chats);
    }

    public boolean isEmpty() {
        return chats.isEmpty();
    }

    public boolean isLastChat() {
        return !isEmpty() && chats.get(chats.size() - 1).getId().equals(1L);
    }

    public Optional<Long> findMinChatId() {
        return chats.stream()
                .map(Chat::getId)
                .min(Long::compare);
    }

    public List<ChatData> createChatDataWithUsers(final List<AgoraUser> agoraUsers) {
        Map<Long, AgoraUser> agoraUserMap = agoraUsers.stream()
                .collect(Collectors.toMap(AgoraUser::getId, agoraUser -> agoraUser));

        return chats.stream()
                .map(chat -> new ChatData(chat, agoraUserMap.get(chat.getAgoraUser().getId())))
                .toList();
    }

    public List<Chat> getChats() {
        return Collections.unmodifiableList(chats);
    }
}
