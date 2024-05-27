package com.attica.athens.global.auth.dto.reqeust;


public record CreateRefreshTokenRequest(
        Long userId,
        String refresh
) {
}
