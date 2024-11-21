package com.attica.athens.global.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.attica.athens.global.auth.jwt.JwtUtils;
import com.attica.athens.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilsTest extends IntegrationTestSupport {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("JWT 생성")
    void testCreateJWT() {
        String ACCESS_TOKEN = "access-token";

        String accessToken = jwtUtils.createAccessToken(1L, "ROLE_USER");
        assertThat(accessToken).isNotNull();
    }
}
