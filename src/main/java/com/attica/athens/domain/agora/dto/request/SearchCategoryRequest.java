package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record SearchCategoryRequest(
    @Pattern(regexp = "^(active|closed)$", message = "허용되지 않는 Status 입니다.")
    String status,
    @NotNull
    Long category,
    Long next
) {

    public List<AgoraStatus> getStatus() {
        if (status.equals("active")) return List.of(AgoraStatus.RUNNING, AgoraStatus.QUEUED);
        return List.of(AgoraStatus.CLOSED);
    }
}
