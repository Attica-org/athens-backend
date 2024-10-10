package com.attica.athens.domain.member.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundMemberException extends CustomException {

    public NotFoundMemberException(Long userId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not found user. userId: " + userId
        );
    }

    public NotFoundMemberException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not found user."
        );
    }
}
