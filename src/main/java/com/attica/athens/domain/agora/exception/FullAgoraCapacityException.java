package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class FullAgoraCapacityException extends CustomException {

    public FullAgoraCapacityException(Long agroaId) {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Agora is full and cannot accept more users. agoraId: " + agroaId
        );
    }
}
