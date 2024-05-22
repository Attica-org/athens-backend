package com.attica.athens.global.security;

import com.attica.athens.domain.token.dao.RefreshRepository;
import com.attica.athens.domain.token.domain.RefreshToken;
import com.attica.athens.domain.token.dto.CreateRefreshTokenRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 600000L; // 10분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86400000L; // 24시간
    private static final int COOKIE_EXPIRATION_TIME = 24*60*60; // 24시간

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
                null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) {

        //유저 정보
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String refresh = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_EXPIRATION_TIME);

        addRefreshEntity(new CreateRefreshTokenRequest(username,refresh,REFRESH_TOKEN_EXPIRATION_TIME));

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshEntity(CreateRefreshTokenRequest createRefreshTokenRequest) {

        String username = createRefreshTokenRequest.username();
        String refresh = createRefreshTokenRequest.refresh();

        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken refreshEntity = RefreshToken.createRefreshToken(username,refresh,date.toString());

        refreshRepository.save(refreshEntity);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
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
