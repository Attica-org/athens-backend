package com.attica.athens.domain.agora.dto.request;

import java.time.Duration;

public record AgoraCreateRequest(
        String title,
        Integer capacity,
        Duration duration,
        String color,
        String code
) {
}
