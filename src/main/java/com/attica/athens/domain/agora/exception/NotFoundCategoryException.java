package com.attica.athens.domain.agora.exception;

import com.attica.athens.domain.common.advice.CustomException;
import org.springframework.http.HttpStatus;

public class NotFoundCategoryException extends CustomException {

    public NotFoundCategoryException(String categoryId) {
        super(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 카테고리입니다. categoryId: " + categoryId
        );
    }
}
