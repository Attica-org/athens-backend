package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class SessionReconnectException extends CustomException {
    public SessionReconnectException(String sessionId) {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "Reconnection attempt failed for sessionId:" + sessionId
        );
    }
}
