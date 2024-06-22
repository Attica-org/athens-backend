package com.attica.athens.domain.agora.vote.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class VoteTimeOutException extends CustomException {
    public VoteTimeOutException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "Voting period has expired."
        );
    }
}
