package com.attica.athens.global.auth.filter;

import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;
import static com.attica.athens.global.auth.jwt.Constants.REQUEST_ATTRIBUTE_NAME;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.dao.RefreshTokenRepository;
import com.attica.athens.global.auth.exception.NotFoundRefreshTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = getRefreshToken(request);

        try {
            if (!(authService.validateToken(refreshToken))) {
                return;
            }
            refreshTokenRepository.existsByRefresh(refreshToken)
                    .orElseThrow(NotFoundRefreshTokenException::new);
        } catch (CustomException e) {
            request.setAttribute(REQUEST_ATTRIBUTE_NAME, e);
        }

        // refreshTokenRepository.deleteByRefresh(refreshToken);

        clearCookie(response, REFRESH_TOKEN);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private String getRefreshToken(HttpServletRequest request) {

        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(NotFoundRefreshTokenException::new);
    }

    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
