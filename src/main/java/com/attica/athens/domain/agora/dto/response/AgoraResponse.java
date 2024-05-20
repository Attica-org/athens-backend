package com.attica.athens.domain.agora.dto.response;

import lombok.Getter;

@Getter
public class AgoraResponse<T> {
    private boolean success;
    private T response;

    public AgoraResponse(T response) {
        this.success = true;
        this.response = response;
    }
}
