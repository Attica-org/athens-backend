package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotParticipateException extends CustomException {

    public NotParticipateException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.RESOURCE_ACCESS_FORBIDDEN,
                "User is not participating in the agora"
        );
    }
}
