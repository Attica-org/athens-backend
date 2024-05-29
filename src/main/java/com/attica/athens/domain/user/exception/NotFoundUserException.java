package com.attica.athens.domain.user.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundUserException extends CustomException {

    public NotFoundUserException(Long userId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not found user. categoryId: " + userId
        );
    }
}
