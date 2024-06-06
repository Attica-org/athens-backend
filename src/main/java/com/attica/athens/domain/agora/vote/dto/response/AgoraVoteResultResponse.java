package com.attica.athens.domain.agora.vote.dto.response;

public record AgoraVoteResultResponse(
        Long id,
        Integer prosCount,
        Integer consCount
) {
}
