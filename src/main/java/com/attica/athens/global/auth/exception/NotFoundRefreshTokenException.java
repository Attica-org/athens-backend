package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundRefreshTokenException extends CustomException {
    public NotFoundRefreshTokenException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.ACCESS_DENIED,
                "Refresh Token Not Exist."
        );
    }
}
