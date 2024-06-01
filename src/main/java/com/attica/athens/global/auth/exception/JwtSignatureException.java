package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class JwtSignatureException extends JwtException {
    public JwtSignatureException() {
        super(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.AUTHENTICATION_FAILED,
                "Invalid JWT signature."
        );
    }
}
