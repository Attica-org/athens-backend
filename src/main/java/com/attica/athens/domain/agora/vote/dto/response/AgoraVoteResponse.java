package com.attica.athens.domain.agora.vote.dto.response;

import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraVoteType;

public record AgoraVoteResponse(Long id, AgoraVoteType voteType) {
    public AgoraVoteResponse(AgoraMember agoraMember) {
        this(agoraMember.getId(), agoraMember.getVoteType());
    }
}
