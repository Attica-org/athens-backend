package com.attica.athens.domain.agora.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import com.attica.athens.domain.agora.domain.Agora;

public record StartAgoraResponse(Long agoraId, String startTime) {

    public StartAgoraResponse(Agora agora) {
        this(agora.getId(), format(agora.getStartTime()));
    }
}
