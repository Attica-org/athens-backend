package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.UserData;
import java.util.List;
import java.util.stream.Collectors;

public record GetChatParticipants(Long agoraId, List<UserData> participants) {
    public GetChatParticipants(List<AgoraUser> agoraUsers, Long agoraId) {
        this(agoraId, getParticipants(agoraUsers));
    }

    private static List<UserData> getParticipants(List<AgoraUser> agoraUsers) {
        return agoraUsers.stream()
                .map(UserData::from)
                .collect(Collectors.toList());
    }
}
