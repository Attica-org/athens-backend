package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundAgoraIdException extends CustomException {

    public NotFoundAgoraIdException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not found agoraId."
        );
    }
}
