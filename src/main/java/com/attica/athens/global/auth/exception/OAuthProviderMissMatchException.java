package com.attica.athens.global.auth.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class OAuthProviderMissMatchException extends CustomException {

    public OAuthProviderMissMatchException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "You've signed up using a different social account. Please use that account to log in.");
    }
}
