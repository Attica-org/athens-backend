package com.attica.athens.domain.agora.vote.dto.request;

import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;

public record AgoraVoteRequest(
        AgoraVoteType voteType,
        Boolean opinionVoted
) {
}
