package com.attica.athens.domain.agora.vote.dto.response;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;

public record AgoraVoteResponse(Long id, AgoraVoteType voteType) {
    public AgoraVoteResponse(AgoraUser agoraUser) {
        this(agoraUser.getId(), agoraUser.getVoteType());
    }
}
