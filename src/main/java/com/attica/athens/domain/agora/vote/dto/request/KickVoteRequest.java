package com.attica.athens.domain.agora.vote.dto.request;

public record KickVoteRequest(Long targetMemberId, int currentMemberCount) {
}
