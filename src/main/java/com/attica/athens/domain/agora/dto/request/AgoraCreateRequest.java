package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.domain.Category;
import java.time.Duration;

public record AgoraCreateRequest(
        String title,
        Integer capacity,
        Duration duration,
        String color,
        Category code
) {
}
