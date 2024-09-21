package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import java.time.LocalDateTime;

public record AgoraExitResponse(
        Long userId,
        AgoraMemberType type,
        LocalDateTime socketDisconnectTime
) {
}
