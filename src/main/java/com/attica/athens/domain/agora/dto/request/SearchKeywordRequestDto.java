package com.attica.athens.domain.agora.dto.request;

public record SearchKeywordRequestDto(
    String agora_name,
    String status,
    Long next
) {
}
