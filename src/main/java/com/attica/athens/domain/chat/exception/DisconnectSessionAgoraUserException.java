package com.attica.athens.domain.chat.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class DisconnectSessionAgoraUserException extends CustomException {
    public DisconnectSessionAgoraUserException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not an agora user connected to the session."
        );
    }
}
