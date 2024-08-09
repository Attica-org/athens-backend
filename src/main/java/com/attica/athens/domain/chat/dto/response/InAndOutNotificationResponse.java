package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.domain.ChatType;

public record InAndOutNotificationResponse(ChatType type, InAndOutAgoraMemberData data) {

    public record InAndOutAgoraMemberData(
            Long agoraId,
            Long memberId,
            String username
    ) {
        public InAndOutAgoraMemberData(Long agoraId, AgoraMember agoraMember) {
            this(agoraId, agoraMember.getId(), agoraMember.getNickname());
        }
    }
}
