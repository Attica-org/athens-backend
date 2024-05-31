package com.attica.athens.domain.chat.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(String status, String message, String code, String timestamp) {

    public ErrorResponse(String message, String code) {
        this("ERROR", message, code, LocalDateTime.now().toString());
    }
}
