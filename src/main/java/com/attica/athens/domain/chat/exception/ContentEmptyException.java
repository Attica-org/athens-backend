package com.attica.athens.domain.chat.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class ContentEmptyException extends CustomException {

    public ContentEmptyException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "Content must not be empty"
        );
    }
}
