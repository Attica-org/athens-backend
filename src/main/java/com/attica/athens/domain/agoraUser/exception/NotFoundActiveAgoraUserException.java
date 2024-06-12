package com.attica.athens.domain.agoraUser.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundActiveAgoraUserException extends CustomException {

    public NotFoundActiveAgoraUserException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not participating in the Agora under discussion."
        );
    }
}
