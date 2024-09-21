package com.attica.athens.domain.member.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class DuplicateMemberException extends CustomException {

    public DuplicateMemberException() {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_RESOURCE,
                "Member already exists"
        );
    }
}
