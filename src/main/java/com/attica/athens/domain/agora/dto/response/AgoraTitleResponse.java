package com.attica.athens.domain.agora.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.domain.AgoraThumbnail;

public record AgoraTitleResponse(String title, AgoraStatus status) {

    public AgoraTitleResponse(Agora agora) {
        this(agora.getTitle(), agora.getStatus());
    }
}
