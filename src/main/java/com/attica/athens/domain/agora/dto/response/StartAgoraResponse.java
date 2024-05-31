package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import java.time.LocalDateTime;

public record StartAgoraResponse(Long agoraId, LocalDateTime startTime) {

    public StartAgoraResponse(Agora agora) {
        this(agora.getId(), agora.getStartTime());
    }
}
