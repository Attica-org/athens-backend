package com.attica.athens.domain.member.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyActivateMemberException extends CustomException {
    public AlreadyActivateMemberException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "You are already an active member."
        );
    }
}
