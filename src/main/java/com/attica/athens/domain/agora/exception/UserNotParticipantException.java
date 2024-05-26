package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotParticipantException extends CustomException {

    public UserNotParticipantException(Long agoraId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "User is not a participant of the agora. AgoraId: " + agoraId
        );
    }
}
