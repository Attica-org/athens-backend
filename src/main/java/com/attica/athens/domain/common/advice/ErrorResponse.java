package com.attica.athens.domain.common.advice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String status;
    private int statusCode;
    private String message;

    private ErrorResponse(String status, int statusCode, String message) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

    public static ErrorResponse of(String status, int statusCode, String message) {
        return new ErrorResponse(status, statusCode, message);
    }
}
