package com.attica.athens.domain.agora.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgoraStatus {
    QUEUED("active"),
    RUNNING("active"),
    CLOSED("closed");

    private final String type;
}
