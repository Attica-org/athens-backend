package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class JwtIllegalArgumentException extends JwtException {
    public JwtIllegalArgumentException() {
        super(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "Invalid JWT token."
        );
    }
}
