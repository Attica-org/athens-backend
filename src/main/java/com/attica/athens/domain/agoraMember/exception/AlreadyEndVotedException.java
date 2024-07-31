package com.attica.athens.domain.agoraMember.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyEndVotedException extends CustomException {

    public AlreadyEndVotedException() {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_RESOURCE,
                "User has already voted for ending the agora"
        );
    }
}
