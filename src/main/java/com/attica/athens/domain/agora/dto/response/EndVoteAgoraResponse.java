package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.Agora;

public record EndVoteAgoraResponse(Long agoraId, Integer endVoteCount) {

    public static EndVoteAgoraResponse createEndVoteAgoraResponse(Agora agora) {
        return new EndVoteAgoraResponse(agora.getId(), agora.getEndVoteCount());
    }
}
