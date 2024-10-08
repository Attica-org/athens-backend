package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class ImageUpdateAccessDeniedException extends CustomException {

    public ImageUpdateAccessDeniedException() {
        super(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.ACCESS_DENIED,
                "This user don't permission to update the thumbnail"
        );
    }
}
