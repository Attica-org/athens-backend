package com.attica.athens.domain.common.advice;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private int status;
    private int code;
    private String message;

    private ErrorResponse(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(int status, int code, String message) {
        return new ErrorResponse(status, code, message);
    }
}
