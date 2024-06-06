package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidAgoraStatusChangeException extends CustomException {

    public InvalidAgoraStatusChangeException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "Invalid agora status transition."
        );
    }
}
