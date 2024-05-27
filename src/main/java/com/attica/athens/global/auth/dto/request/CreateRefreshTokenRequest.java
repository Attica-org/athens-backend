package com.attica.athens.global.auth.dto.request;


public record CreateRefreshTokenRequest(
        Long userId,
        String refresh
) {
}
