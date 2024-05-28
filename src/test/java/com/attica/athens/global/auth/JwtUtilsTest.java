package com.attica.athens.global.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.attica.athens.global.auth.jwt.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtUtilsTest {

    @Test
    @DisplayName("JWT 생성")
    void testCreateJWT() {
        String ACCESS_TOKEN = "access-token";

        JwtUtils jwtUtils = new JwtUtils(
                "123213213213213213213213131232132131232132132122", 3600, 86400000);

        String accessToken = jwtUtils.createJwtToken(ACCESS_TOKEN, 1L, "ROLE_USER");
        assertThat(accessToken).isNotNull();
    }
}