package com.attica.athens.domain.chat.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class WriterReactionException extends CustomException {

    public WriterReactionException() {
        super(
                HttpStatus.FORBIDDEN,
                ErrorCode.RESOURCE_ACCESS_FORBIDDEN,
                "Chat writers cannot respond to themselves."
        );
    }
}
