package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class SessionReconnectException extends CustomException {
    public SessionReconnectException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.MISSING_PART,
                "Need to import a chat history."
        );
    }
}
