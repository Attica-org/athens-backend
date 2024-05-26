package com.attica.athens.global.security.refresh.application;

import static com.attica.athens.global.security.jwt.JWTUtil.getId;
import static com.attica.athens.global.security.jwt.JWTUtil.getRole;
import static com.attica.athens.global.security.jwt.JWTUtil.isExpired;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import com.attica.athens.global.security.jwt.JWTUtil;
import com.attica.athens.global.security.refresh.dao.RefreshRepository;
import com.attica.athens.global.security.refresh.domain.RefreshToken;
import com.attica.athens.global.security.refresh.dto.CreateCookieResponse;
import com.attica.athens.global.security.refresh.dto.CreateRefreshTokenRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L;
    private static final int COOKIE_EXPIRATION_TIME = 24 * 60 * 60;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;


    public CreateCookieResponse reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = getRefreshToken(request);

        if (isExpired(refreshToken)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.WRONG_REQUEST_TRANSMISSION, "Expired Token");
        }

        Boolean isRefreshTokenExist = refreshRepository.existsByRefresh(refreshToken);

        if (!isRefreshTokenExist) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.RESOURCE_NOT_FOUND, "RefreshToken Not Exist");
        }

        Long userId = getId(refreshToken);
        String role = getRole(refreshToken);

        String newAccess = jwtUtil.createJwt("access", userId, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, REFRESH_TOKEN_EXPIRATION_TIME);

        refreshRepository.deleteByRefresh(refreshToken);

        createRefreshEntity(new CreateRefreshTokenRequest(userId, newRefresh, REFRESH_TOKEN_EXPIRATION_TIME));

        Cookie[] cookies = new Cookie[2];

        cookies[0] = createCookie("access", newAccess);
        cookies[1] = createCookie("refresh", newRefresh);
        return new CreateCookieResponse(cookies);

    }

    private String getRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PART,
                        "Refresh Token not found"));
    }


    private void createRefreshEntity(CreateRefreshTokenRequest createRefreshTokenRequest) {

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
