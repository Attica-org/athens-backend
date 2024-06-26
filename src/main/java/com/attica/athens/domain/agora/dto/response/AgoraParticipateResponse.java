package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agoraUser.domain.AgoraUserType;

public record AgoraParticipateResponse(
        Long agoraId,
        Long userId,
        AgoraUserType type
) {
}
