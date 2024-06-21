package com.attica.athens.domain.agora.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgoraCreateRequest(
        @NotBlank
        String title,
        @Min(1)
        Integer capacity,
        @Min(1) @Max(180)
        Integer duration,
        @NotBlank
        String color,
        @NotNull
        Long categoryId
) {
}
