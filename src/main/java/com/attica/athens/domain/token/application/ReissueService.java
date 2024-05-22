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

import java.util.Date;

@Service
public class ReissueService {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L; // 24시간

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueService(JWTUtil jwtUtil,RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 로그아웃?
            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getId(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_EXPIRATION_TIME);

//        //Refresh 토큰 Cookie 값 0
//        Cookie refreshCookie = new Cookie("refresh", null);
//        refreshCookie.setMaxAge(0);
//        refreshCookie.setPath("/");
//
//        response.addCookie(refreshCookie);
//
//        Cookie accessCookie = new Cookie("access", null);
//        accessCookie.setMaxAge(0);
//        accessCookie.setPath("/");
//        추후 db에 만료된 토큰이 쌓이는 것을 지워줘야한다.
//        response.addCookie(accessCookie);
        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);

        String expiredRefreshTokenExpiredTime =  String.valueOf(REFRESH_TOKEN_EXPIRATION_TIME);

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
