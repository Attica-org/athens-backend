package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidAuthorizationHeaderException extends CustomException {

    public InvalidAuthorizationHeaderException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.MISSING_PART,
                "Authorization header is missing or does not start with Bearer"
        );
    }
}
