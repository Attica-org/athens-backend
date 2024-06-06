package com.attica.athens.domain.agora.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.chat.domain.ChatType;

public record StartNotificationResponse(ChatType type, StartAgoraData data) {

    public record StartAgoraData(
            Long agoraId,
            String startTime
    ) {
        public StartAgoraData(Agora agora) {
            this(agora.getId(), format(agora.getStartTime()));
        }
    }
}
