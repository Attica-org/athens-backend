package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.domain.ChatType;

public record EnterNotificationResponse(ChatType type, EnterAgoraMemberData data) {

    public record EnterAgoraMemberData(
            Long agoraId,
            Long memberId,
            String nickname
    ) {
        public EnterAgoraMemberData(Agora agora, AgoraMember agoraMember) {
            this(agora.getId(), agoraMember.getId(), agoraMember.getNickname());
        }
    }

}
