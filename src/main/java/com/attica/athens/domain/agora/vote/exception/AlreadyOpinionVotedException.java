package com.attica.athens.domain.agora.vote.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyOpinionVotedException extends CustomException {

    public AlreadyOpinionVotedException() {
        super(
                HttpStatus.BAD_REQUEST,
                ErrorCode.WRONG_REQUEST_TRANSMISSION,
                "User has already voted for Opinion in this agora"
        );
    }
}
