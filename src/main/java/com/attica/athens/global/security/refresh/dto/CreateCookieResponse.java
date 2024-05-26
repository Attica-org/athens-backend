package com.attica.athens.global.security.refresh.dto;

import jakarta.servlet.http.Cookie;

public record CreateCookieResponse(
        Cookie[] cookie
) {
}
