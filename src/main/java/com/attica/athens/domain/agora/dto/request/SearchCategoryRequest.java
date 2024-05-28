package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;

public record SearchCategoryRequest(
        @Pattern(regexp = "^(active|closed)$", message = "허용되지 않는 Status 입니다.")
        String status,
        @NotNull
        Long category,
        Long next
) {

    public List<AgoraStatus> getStatus() {
        return Arrays.stream(AgoraStatus.values())
                .filter(agoraStatus -> agoraStatus.getType().equals("active"))
                .toList();
    }
}
