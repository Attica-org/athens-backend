package com.attica.athens.global.security;

import static com.attica.athens.global.security.JWTUtil.createAuthentication;
import static com.attica.athens.global.security.JWTUtil.getId;
import static com.attica.athens.global.security.JWTUtil.getRole;
import static com.attica.athens.global.security.JWTUtil.isExpired;
import static com.attica.athens.global.security.JWTUtil.resolveToken;

import com.attica.athens.global.config.SecurityConfig;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isNonProtectedUrl(request)) {
            filterChain.doFilter(request, response);

            return;
        }

        String token = resolveToken(request);

        if (isExpired(token)) {
            filterChain.doFilter(request, response);

            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        Long id = getId(token);
        String role = getRole(token);

        Authentication authentication = createAuthentication(id, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
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
