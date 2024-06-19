package com.attica.athens.domain.agora.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;

public record EndVoteAgoraResponse(Long agoraId, Integer endVoteCount, Boolean isClosed, String endTime) {

    public EndVoteAgoraResponse(Agora agora) {
        this(agora.getId(), agora.getEndVoteCount(), agora.getStatus() == AgoraStatus.CLOSED,
                agora.getEndTime() == null ? null : format(agora.getEndTime()));
    }
}
