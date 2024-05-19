package com.attica.athens.domain.agora.dto.request;

public record AgoraCreateRequest(
        String title,
        Integer capacity,
        Integer duration,
        String color,
        String code
) {
}
