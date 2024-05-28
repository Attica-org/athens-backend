package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class JwtUnsupportedJwtException extends CustomException {
    public JwtUnsupportedJwtException() {
        super(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.AUTHENTICATION_FAILED,
                "Unsupported JWT token."
        );
    }
}
