package com.attica.athens.domain.common.advice;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
