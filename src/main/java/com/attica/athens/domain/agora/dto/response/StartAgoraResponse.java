package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import java.time.LocalDateTime;

public record StartAgoraResponse(Long agoraId, LocalDateTime startTime) {

    public static StartAgoraResponse createAgoraStartResponse(Agora agora) {
        return new StartAgoraResponse(agora.getId(), agora.getStartTime());
    }
}
