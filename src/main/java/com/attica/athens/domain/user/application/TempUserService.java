package com.attica.athens.domain.user.application;

import static com.attica.athens.global.security.JWTUtil.createJwt;

import com.attica.athens.domain.token.dao.RefreshRepository;
import com.attica.athens.domain.token.domain.RefreshToken;
import com.attica.athens.domain.token.dto.CreateRefreshTokenRequest;
import com.attica.athens.domain.user.dao.TempUserRepository;
import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.domain.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L;
    private static final int COOKIE_EXPIRATION_TIME = 24 * 60 * 60;

    private final TempUserRepository tempUserRepository;
    private final RefreshRepository refreshRepository;

    @Transactional
    public String createTempUser(HttpServletResponse response) {

        TempUser tempUser = TempUser.createTempUser();

        tempUserRepository.save(tempUser);

        String access = createJwt("access", tempUser.getId(), UserRole.ROLE_TEMP_USER.name(),
                ACCESS_TOKEN_EXPIRATION_TIME);
        String refresh = createJwt("refresh", tempUser.getId(), UserRole.ROLE_TEMP_USER.name(),
                REFRESH_TOKEN_EXPIRATION_TIME);

        addRefreshEntity(
                new CreateRefreshTokenRequest(tempUser.getId(), refresh, REFRESH_TOKEN_EXPIRATION_TIME));

        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        return createJwt("access", tempUser.getId(), UserRole.ROLE_TEMP_USER.name(), 600000L);
    }

    private void addRefreshEntity(CreateRefreshTokenRequest createRefreshTokenRequest) {

        Long userId = createRefreshTokenRequest.userId();
        String refresh = createRefreshTokenRequest.refresh();

        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken refreshEntity = RefreshToken.createRefreshToken(userId, refresh, date);

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(COOKIE_EXPIRATION_TIME);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
