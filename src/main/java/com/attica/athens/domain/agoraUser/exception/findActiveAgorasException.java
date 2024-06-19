package com.attica.athens.domain.agoraUser.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class findActiveAgorasException extends CustomException {
    public findActiveAgorasException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "There are more than one agora in progress."
        );
    }
}
