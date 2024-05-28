package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class JwtExpiredException extends CustomException {

    public JwtExpiredException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "The token has expired."
        );
    }
}
