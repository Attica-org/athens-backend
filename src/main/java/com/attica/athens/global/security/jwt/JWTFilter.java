package com.attica.athens.global.security.jwt;

import static com.attica.athens.global.security.jwt.JWTUtil.createAuthentication;
import static com.attica.athens.global.security.jwt.JWTUtil.getId;
import static com.attica.athens.global.security.jwt.JWTUtil.getRole;
import static com.attica.athens.global.security.jwt.JWTUtil.isExpired;

import com.attica.athens.global.config.SecurityConfig;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final static String ACCESS_TOKEN_COOKIE_NAME = "access";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = getAccessToken(request);

        if (isNonProtectedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isExpired(accessToken)) {
            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        Long id = getId(accessToken);
        String role = getRole(accessToken);

        Authentication authentication = createAuthentication(id, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }


    private String getAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElse(null);
    }

    private boolean isNonProtectedUrl(HttpServletRequest request) {

        for (String urlPattern : SecurityConfig.PUBLIC_URLS) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(urlPattern);

            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}

