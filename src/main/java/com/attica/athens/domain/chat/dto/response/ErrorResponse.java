package com.attica.athens.domain.chat.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(String status, String message, String code, String timestamp) {

    public static ErrorResponse from(String message, String code) {
        return new ErrorResponse("ERROR", message, code, LocalDateTime.now().toString());
    }
}
