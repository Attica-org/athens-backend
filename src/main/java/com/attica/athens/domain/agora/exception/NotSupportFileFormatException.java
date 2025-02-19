package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotSupportFileFormatException extends CustomException {

    public NotSupportFileFormatException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "This is an unsupported file type."
        );
    }
}
