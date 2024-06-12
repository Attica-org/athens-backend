package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class DuplicatedNicknameException extends CustomException {

    public DuplicatedNicknameException(String nickname) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_RESOURCE,
                "The nickname is already in use. nickname: " + nickname
        );
    }
}
