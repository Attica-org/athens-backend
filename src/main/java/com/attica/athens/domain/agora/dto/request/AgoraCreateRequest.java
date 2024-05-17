package com.attica.athens.domain.agora.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

public record AgoraCreateRequest(
        String title,
        @Min(1)
        Integer capacity,
        Duration duration,
        @NotEmpty @NotBlank
        String color,
        @NotNull
        Long categoryId
) {
}
