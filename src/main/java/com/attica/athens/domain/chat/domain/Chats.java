package com.attica.athens.domain.chat.domain;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
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

    public List<ChatData> createChatDataWithUsers(final List<AgoraMember> agoraMembers,
                                                  final Map<Long, Map<ReactionType, Long>> reactionCounts) {
        Map<Long, AgoraMember> agoraMemberMap = agoraMembers.stream()
                .collect(Collectors.toMap(AgoraMember::getId, agoraMember -> agoraMember));

        return chats.stream()
                .map(chat -> new ChatData(chat, agoraMemberMap.get(chat.getAgoraMember().getId()),
                        reactionCounts.get(chat.getId())))
                .toList();
    }

    public List<Chat> getChats() {
        return Collections.unmodifiableList(chats);
    }
}
