package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidAgoraStatusException extends CustomException {

    public InvalidAgoraStatusException(AgoraStatus expectedAgoraStatus) {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "Agora status must be " + expectedAgoraStatus
        );
    }
}
