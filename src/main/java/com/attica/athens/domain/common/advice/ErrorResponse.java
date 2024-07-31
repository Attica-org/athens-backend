package com.attica.athens.domain.common.advice;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private int code;
    private Object message;

    public ErrorResponse(int code, Object message) {
        this.code = code;
        this.message = message;
    }
}
