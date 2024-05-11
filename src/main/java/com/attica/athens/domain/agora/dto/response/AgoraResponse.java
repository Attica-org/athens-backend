package com.attica.athens.domain.agora.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AgoraResponse<T> {
    private boolean success;
    private T response;

    @Builder
    public AgoraResponse(boolean success, T response) {
        this.success = success;
        this.response = response;
    }
}
