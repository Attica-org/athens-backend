package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.AgoraStatus;

public record AgoraTitleResponse(
        String title,
        AgoraStatus status
) {
}
