package com.attica.athens.domain.token.application;

import com.attica.athens.domain.token.dao.RefreshRepository;
import com.attica.athens.domain.token.domain.RefreshToken;
import com.attica.athens.domain.token.dto.CreateRefreshTokenRequest;
import com.attica.athens.global.security.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class ReissueService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L; // 24시간
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueService(JWTUtil jwtUtil,RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 refresh token 가져오기
        String refreshToken = Optional.ofNullable(request.getCookies())
                .map(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null))
                .orElse(null);

        if (refreshToken == null) {
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            // 로그아웃?
            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_EXPIRATION_TIME);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refreshToken);

        addRefreshEntity(new CreateRefreshTokenRequest(username, newRefresh,REFRESH_TOKEN_EXPIRATION_TIME));

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("access", newAccess));
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
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

        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
