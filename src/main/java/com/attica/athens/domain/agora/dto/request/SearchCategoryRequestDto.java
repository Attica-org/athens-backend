package com.attica.athens.domain.agora.dto.request;

public record SearchCategoryRequestDto(
    String status,
    String category,
    Long next
) {
}
