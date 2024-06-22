package com.attica.athens.domain.agora.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;

public record EndAgoraResponse(Long agoraId, Boolean isClosed, String endTime) {
    public EndAgoraResponse(Agora agora) {
        this(agora.getId(), agora.getStatus() == AgoraStatus.CLOSED,
                format(agora.getEndTime()));
    }
}
