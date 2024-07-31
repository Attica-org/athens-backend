package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class ObserverException extends CustomException {

    public ObserverException() {
        super(
                HttpStatus.FORBIDDEN,
                ErrorCode.RESOURCE_ACCESS_FORBIDDEN,
                "Observer cannot send this request"
        );
    }
}
