package com.attica.athens.domain.token.domain;

import static org.assertj.core.api.BDDAssertions.then;

import com.attica.athens.global.auth.domain.RefreshToken;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("토큰 테스트")
public class RefreshTokenTest {

    static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJpZCI6InVzZXIyIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcxNjQ3ODE4NiwiZXhwIjoxNzE2NTY0NTg2fQ.rsIz3g2fb2ZXVaDbq_hD8Uh-HdqgYHmyLuiZZFJDnbM";

    @Test
    @DisplayName("리프레시 토큰을 생성한다.")
    void 성공_리프레시토큰생성_유효한파라미터전달() {
        //given
        long userId = 1L;
        Date expirationDate = calculateExpirationDate(1);

        RefreshToken refreshTokenAccess = RefreshToken.builder().build();

        //when
        RefreshToken refreshToken = refreshTokenAccess.createRefreshToken(userId, REFRESH_TOKEN, expirationDate);

        //then
        then(refreshToken.getUserId()).isEqualTo(userId);
        then(refreshToken.getRefresh()).isEqualTo(REFRESH_TOKEN);
        then(refreshToken.getExpiration()).isEqualTo(expirationDate);

    }

    private Date calculateExpirationDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }
}
