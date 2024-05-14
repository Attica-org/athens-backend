package com.attica.athens.domain.agora.domain;

import java.util.Arrays;
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
        return Arrays.stream(AgoraStatus.values())
            .filter(value -> value.getStatus().equals(status))
            .findAny()
            .orElseThrow(() -> new RuntimeException("status not found"));
    }
}
