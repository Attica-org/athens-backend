package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.UserData;
import java.util.List;
import java.util.stream.Collectors;

public record GetChatParticipants(Long agoraId, List<UserData> participants, String agoraThumbnailUrl) {
    public GetChatParticipants(List<AgoraMember> agoraMembers, Long agoraId, String agoraThumbnailUrl) {
        this(agoraId, getParticipants(agoraMembers), agoraThumbnailUrl);
    }

    private static List<UserData> getParticipants(List<AgoraMember> agoraMembers) {
        return agoraMembers.stream()
                .map(UserData::new)
                .collect(Collectors.toList());
    }
}
