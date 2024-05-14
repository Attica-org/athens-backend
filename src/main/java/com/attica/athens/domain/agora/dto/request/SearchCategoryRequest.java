package com.attica.athens.domain.agora.dto.request;

public record SearchCategoryRequest(
    String status,
    String category,
    Long next
) {
}
