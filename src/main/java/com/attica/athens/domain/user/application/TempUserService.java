package com.attica.athens.domain.user.application;

import com.attica.athens.domain.token.dao.RefreshRepository;
import com.attica.athens.domain.token.domain.RefreshToken;
import com.attica.athens.domain.token.dto.CreateRefreshTokenRequest;
import com.attica.athens.domain.user.dao.TempUserRepository;
import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.domain.UserRole;
import com.attica.athens.global.security.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L; // 24시간
    private static final int COOKIE_EXPIRATION_TIME = 24*60*60; // 24시간

    private final TempUserRepository tempUserRepository;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public String createTempUser(HttpServletResponse response) {

        TempUser tempUser = TempUser.createTempUser();

        tempUserRepository.save(tempUser);

        String access = jwtUtil.createJwt("access", tempUser.getUuid().toString(), UserRole.ROLE_TEMP_USER.name(), ACCESS_TOKEN_EXPIRATION_TIME);
        String refresh = jwtUtil.createJwt("refresh", tempUser.getUuid().toString(), UserRole.ROLE_TEMP_USER.name(), REFRESH_TOKEN_EXPIRATION_TIME);

        //DB Refresh Token 저장
        addRefreshEntity(new CreateRefreshTokenRequest(tempUser.getUuid().toString(),refresh,REFRESH_TOKEN_EXPIRATION_TIME));

        //응답 설정
        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
        // 임시
        return jwtUtil.createJwt("access",tempUser.getUuid().toString(), UserRole.ROLE_TEMP_USER.name(),600000L);
    }

    private void addRefreshEntity(CreateRefreshTokenRequest createRefreshTokenRequest) {

        String username = createRefreshTokenRequest.username();
        String refresh = createRefreshTokenRequest.refresh();

        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken refreshEntity = RefreshToken.createRefreshToken(username,refresh,date.toString());

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
