package com.attica.athens.global.security.token.dto;

import jakarta.servlet.http.Cookie;

public record CreateCookieResponse(
        Cookie[] cookie
) {
}
