package com.attica.athens.domain.token;

import static com.attica.athens.global.auth.jwt.Constants.AUTHORITY_KEY;
import static com.attica.athens.global.auth.jwt.Constants.AUTHORITY_ROLE;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.jwt.JwtUtils;
import com.attica.athens.support.IntegrationTestSupport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("토큰 통합 테스트")
public class RefreshTokenApiIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    AuthService authService;

    private SecretKey secretKey;

    @Test
    @DisplayName("토큰을 재발급한다.")
    void 성공_토큰재발급_유효한파라미터전달() throws Exception {
        // given
        String refreshToken = jwtUtils.createRefreshToken(10L, "ROLE_TEMP_USER");

        // when
        final ResultActions result = mockMvc.perform(
                post("/{prefix}/reissue", API_V1_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("Refresh-Token", refreshToken))
        );

        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.success").value(true),
                        jsonPath("$.response").value(Matchers.matchesRegex("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$")),
                        jsonPath("$.error").value(nullValue())
                );
    }

    @Test
    @DisplayName("토큰 재발급시 쿠키헤더값이 존재하지 않으면 예외를 발생시킨다.")
    void 실패_쿠키헤더값_비어있는쿠키헤더() throws Exception {
        // when
        final ResultActions result = mockMvc.perform(
                post("/{prefix}/reissue", API_V1_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.success").value(false),
                        jsonPath("$.response").value(nullValue()),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error.code").value(1202),
                        jsonPath("$.error.message").value("Refresh Token Not Exist.")
                );
    }

    @Test
    @DisplayName("토큰이 만료되었다면 예외를 발생시킨다.")
    void 실패_토큰만료_MAXAGE0으로설정(@Value("${auth.jwt.secret-key}") String secret) throws Exception {
        // given
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm());

        String refreshToken = createJwtToken(10L, "ROLE_TEMP_USER");

        // when
        final ResultActions result = mockMvc.perform(
                post("/{prefix}/reissue", API_V1_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("Refresh-Token", refreshToken))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.success").value(false),
                        jsonPath("$.response").value(nullValue()),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error.code").value(1002),
                        jsonPath("$.error.message").value("The token has expired.")
                );
    }

    @Test
    @DisplayName("토큰의 형식이 잘못되었다면 예외를 발생시킨다.")
    void 실패_토큰형식_문자추가() throws Exception {
        // given
        String refreshToken = jwtUtils.createRefreshToken(10L, "ROLE_TEMP_USER");

        // when
        final ResultActions result = mockMvc.perform(
                post("/{prefix}/reissue", API_V1_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("Refresh-Token", refreshToken + "."))
        );

        // then
        result.andExpect(status().isUnauthorized())
                .andExpectAll(
                        jsonPath("$.success").value(false),
                        jsonPath("$.response").value(nullValue()),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error.code").value(1201),
                        jsonPath("$.error.message").value("Invalid JWT signature.")
                );
    }

    public String createJwtToken(Long id, String role) {
        Date now = new Date();
        Date expire;

        expire = new Date(now.getTime() + 0);
        return Jwts.builder()
                .claim(AUTHORITY_KEY, String.valueOf(id))
                .claim(AUTHORITY_ROLE, role)
                .issuedAt(now)
                .expiration(expire)
                .subject(REFRESH_TOKEN)
                .signWith(secretKey)
                .compact();

    }
}
