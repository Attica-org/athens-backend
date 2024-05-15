package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.domain.AgoraStatus;

public record SearchCategoryRequest(
    AgoraStatus status,
    String category,
    Long next
) {
}
