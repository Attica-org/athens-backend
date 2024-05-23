package com.attica.athens.domain.token.dto;


public record CreateRefreshTokenRequest(
        String username,
        String refresh,
        Long expiration
) {
}
