package com.attica.athens.global.security;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.domain.common.advice.ErrorCode;
import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.domain.User;
import com.attica.athens.domain.user.domain.UserRole;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final static String ACCESS_TOKEN_COOKIE_NAME = "access";

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 쿠키에서 access token 가져오기
        String accessToken = Optional.ofNullable(request.getCookies())
                .map(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null))
                .orElse(null);


        if (isNonProtectedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 확인
        if (jwtUtil.isExpired(accessToken)) {

            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        String id = jwtUtil.getId(accessToken);
        String role = jwtUtil.getRole(accessToken);

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
    private boolean isAccessToken(String category){
        return category.equals(ACCESS_TOKEN_COOKIE_NAME);
    }

    private Authentication createAuthentication(String id, String role) {

        CustomUserDetails customUserDetails =
                role.equals(UserRole.ROLE_TEMP_USER.name())
                        ? new CustomUserDetails(TempUser.createTempUser())
                        : new CustomUserDetails(User.createUser(id, "fakePassword"));

        return new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
    }

}
