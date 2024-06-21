package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends CustomException {

    public InvalidRequestException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "The resource you requested cannot be found. Please check the URL");
    }
}
