package com.attica.athens.domain.common.advice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int statusCode;
    private String message;

    private ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static ErrorResponse of(int statusCode, String message) {
        return new ErrorResponse(statusCode, message);
    }
}
