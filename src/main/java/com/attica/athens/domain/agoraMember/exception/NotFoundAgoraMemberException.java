package com.attica.athens.domain.agoraMember.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundAgoraMemberException extends CustomException {

    public NotFoundAgoraMemberException(Long agoraId, Long userId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Agora user not found with agora id: " + agoraId +
                        " user id: " + userId
        );
    }

    public NotFoundAgoraMemberException(String sessionId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Agora user not found with sessionId: " + sessionId
        );
    }
}
