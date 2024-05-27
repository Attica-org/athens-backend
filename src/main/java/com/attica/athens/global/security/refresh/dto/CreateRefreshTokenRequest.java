package com.attica.athens.global.security.refresh.dto;


public record CreateRefreshTokenRequest(
        Long userId,
        String refresh,
        Long expiration
) {
}
