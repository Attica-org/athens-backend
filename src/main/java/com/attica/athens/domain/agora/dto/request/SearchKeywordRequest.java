package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.domain.AgoraStatus;

public record SearchKeywordRequest(
    String agoraName,
    AgoraStatus status,
    Long next
) {
}
