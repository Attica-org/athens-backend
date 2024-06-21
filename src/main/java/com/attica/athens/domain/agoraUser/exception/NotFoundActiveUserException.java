package com.attica.athens.domain.agoraUser.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundActiveUserException extends CustomException {
    public NotFoundActiveUserException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "No users are participating in Agora."
        );
    }
}
