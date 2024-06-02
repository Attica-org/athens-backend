package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyParticipateException extends CustomException {

    public AlreadyParticipateException(Long agoraId, Long userId) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_RESOURCE,
                "User has already participated. agoraId: " + agoraId + ", userId: " + userId
        );
    }
}
