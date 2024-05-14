package com.attica.athens.domain.agora.dto.request;

public record SearchKeywordRequest(
    String agora_name,
    String status,
    Long next
) {

    public String agoraName() {
        return agora_name;
    }
}
