package com.attica.athens.domain.agora.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.chat.domain.ChatType;

public record EndNotificationResponse(ChatType type, EndAgoraData data) {

    public record EndAgoraData(
            Long agoraId,
            String endTime
    ) {
        public EndAgoraData(Agora agora) {
            this(agora.getId(), format(agora.getEndTime()));
        }
    }
}
