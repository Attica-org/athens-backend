package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class JwtUnsupportedJwtException extends JwtException {
    public JwtUnsupportedJwtException() {
        super(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.AUTHENTICATION_FAILED,
                "Unsupported JWT token."
        );
    }
}
