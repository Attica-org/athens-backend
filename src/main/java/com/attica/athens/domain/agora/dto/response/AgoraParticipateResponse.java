package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;

public record AgoraParticipateResponse(
        Long agoraId,
        Long userId,
        AgoraMemberType type,
        boolean isCreator
) {
}
