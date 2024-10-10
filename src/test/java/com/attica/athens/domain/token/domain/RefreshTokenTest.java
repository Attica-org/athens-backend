package com.attica.athens.domain.token.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.attica.athens.global.auth.domain.RefreshToken;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("토큰 테스트")
public class RefreshTokenTest {

    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJpZCI6InVzZXIyIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcxNjQ3ODE4NiwiZXhwIjoxNzE2NTY0NTg2fQ.rsIz3g2fb2ZXVaDbq_hD8Uh-HdqgYHmyLuiZZFJDnbM";

    @Test
    @DisplayName("리프레시 토큰을 생성한다.")
    void 성공_리프레시토큰생성_유효한파라미터전달() {
        // given
        Long userId = 1L;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusDays(1);

        // when
        RefreshToken refreshToken = new RefreshToken(userId, REFRESH_TOKEN, expirationDateTime);

        // then
        assertThat(refreshToken.getUserId()).isEqualTo(userId);
        assertThat(refreshToken.getRefresh()).isEqualTo(REFRESH_TOKEN);
        assertThat(refreshToken.getExpirationDateTime()).isEqualTo(expirationDateTime);
        assertThat(refreshToken.isExpired()).isFalse();
    }

    @Test
    @DisplayName("만료된 리프레시 토큰을 확인한다.")
    void 성공_리프레시토큰만료확인() {
        // given
        Long userId = 1L;
        LocalDateTime expiredDateTime = LocalDateTime.now().minusDays(1);

        // when
        RefreshToken refreshToken = new RefreshToken(userId, REFRESH_TOKEN, expiredDateTime);

        // then
        assertThat(refreshToken.isExpired()).isTrue();
    }
}
