package com.attica.athens.domain.agoraUser.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundAgoraUserException extends CustomException {

    public NotFoundAgoraUserException(Long agoraId, Long userId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Agora user not found with agora id: " + agoraId +
                        "user id: " + userId
        );
    }
}
