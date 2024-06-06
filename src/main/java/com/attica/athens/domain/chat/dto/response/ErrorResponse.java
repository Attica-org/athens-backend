package com.attica.athens.domain.chat.dto.response;

import static com.attica.athens.global.utils.TimeFormatter.format;

import java.time.LocalDateTime;

public record ErrorResponse(String status, Integer code, String message, String timestamp) {

    public ErrorResponse(Integer code, String message) {
        this("ERROR", code, message, format(LocalDateTime.now()));
    }
}
