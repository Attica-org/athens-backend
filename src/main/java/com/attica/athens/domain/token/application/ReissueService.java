package com.attica.athens.domain.token.application;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import com.attica.athens.domain.token.dao.RefreshRepository;
import com.attica.athens.domain.token.domain.RefreshToken;
import com.attica.athens.domain.token.dto.CreateRefreshTokenRequest;
import com.attica.athens.global.security.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class ReissueService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L;
    private static final int COOKIE_EXPIRATION_TIME = 24 * 60 * 60;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueService(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = getRefreshToken(request);

        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.WRONG_REQUEST_TRANSMISSION, "Expired Token");
        }

        Boolean isRefreshTokenExist = refreshRepository.existsByRefresh(refreshToken);

        if (!isRefreshTokenExist) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.RESOURCE_NOT_FOUND, "RefreshToken Not Exist");
        }

        String username = jwtUtil.getId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccess = jwtUtil.createJwt("access", username, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_EXPIRATION_TIME);

        refreshRepository.deleteByRefresh(refreshToken);

        addRefreshEntity(new CreateRefreshTokenRequest(username, newRefresh, REFRESH_TOKEN_EXPIRATION_TIME));

        response.addCookie(createCookie("access", newAccess));
        response.addCookie(createCookie("refresh", newRefresh));
    }

    private static String getRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies()).flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PART,
                        "Refresh Token not found"));
    }

    private void addRefreshEntity(CreateRefreshTokenRequest createRefreshTokenRequest) {

        String username = createRefreshTokenRequest.username();
        String refresh = createRefreshTokenRequest.refresh();

        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken refreshEntity = RefreshToken.createRefreshToken(username, refresh, date.toString());

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
