package com.attica.athens.global.auth.dto.request;


public record CreateRefreshTokenRequest(
        String userId,
        String refresh
) {
}
