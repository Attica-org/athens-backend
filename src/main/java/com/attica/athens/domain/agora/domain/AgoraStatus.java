package com.attica.athens.domain.agora.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgoraStatus {
    QUEUED("queued"),
    RUNNING("active"),
    CLOSED("closed")
    ;

    private final String status;

    public static AgoraStatus of(String status) {
        for (AgoraStatus agoraStatus : values()) {
            if (agoraStatus.getStatus().equals(status)) {
                return agoraStatus;
            }
        }
        return null;
    }
}
