package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NullFieldException extends CustomException {

    public NullFieldException(String fieldName) {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.MISSING_PART,
                "The field " + fieldName + " must not be null"
        );
    }
}
