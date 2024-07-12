package com.attica.athens.domain.chat.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class ContentExceedException extends CustomException {

    public ContentExceedException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "Content length exceeds maximum limit of 10000 characters"
        );
    }
}
