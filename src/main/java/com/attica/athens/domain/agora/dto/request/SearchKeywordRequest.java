package com.attica.athens.domain.agora.dto.request;

import static com.attica.athens.domain.agora.domain.AgoraStatus.CLOSED;
import static com.attica.athens.domain.agora.domain.AgoraStatus.QUEUED;
import static com.attica.athens.domain.agora.domain.AgoraStatus.RUNNING;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record SearchKeywordRequest(
        @Pattern(regexp = "^(active|closed)$", message = "허용되지 않는 Status 입니다.")
        String status,
        Long next
) {

    public List<AgoraStatus> getStatus() {
        return status.equals(CLOSED.getType()) ?
                List.of(CLOSED) :
                List.of(QUEUED, RUNNING);
    }
}
