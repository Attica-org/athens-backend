package com.attica.athens.domain.agora.vote.dto.response;

import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;

public record AgoraVoteResponse(
        Long id,
        AgoraVoteType voteType
) {
}
