package com.attica.athens.domain.chat.exception;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundChatException extends CustomException {

    public NotFoundChatException(Long chatId) {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND,
                "Not found chat. chatId: " + chatId
        );
    }
}
