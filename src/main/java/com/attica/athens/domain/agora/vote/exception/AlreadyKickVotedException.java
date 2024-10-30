package com.attica.athens.domain.agora.vote.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyKickVotedException extends CustomException {

    public AlreadyKickVotedException(Long targetMemberId) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_RESOURCE,
                "이미 투표한 투표한 대상입니다. targetMemberId: " + targetMemberId
        );
    }
}
