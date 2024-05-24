package com.attica.athens.domain.token.dto;


public record CreateRefreshTokenRequest(
        Long userId,
        String refresh,
        Long expiration
) {
}
