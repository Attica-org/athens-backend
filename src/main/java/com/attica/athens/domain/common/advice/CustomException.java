package com.attica.athens.domain.common.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final int errorCode;

    public CustomException(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getCode();
        this.httpStatus = httpStatus;
    }
}
