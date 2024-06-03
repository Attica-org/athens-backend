package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;

public record EndVoteAgoraResponse(Long agoraId, Integer endVoteCount, Boolean isClosed) {

    public EndVoteAgoraResponse(Agora agora) {
        this(agora.getId(), agora.getEndVoteCount(), agora.getStatus() == AgoraStatus.CLOSED);
    }
}
