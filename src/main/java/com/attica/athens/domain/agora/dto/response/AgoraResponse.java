package com.attica.athens.domain.agora.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AgoraResponse<T> {
    private boolean success;
    private T response;

    @Builder
    public AgoraResponse(T response) {
        this.success = true;
        this.response = response;
    }
}
