package com.attica.athens.global.auth.filter;

import static com.attica.athens.global.auth.jwt.Constants.ACCESS_TOKEN;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;

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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

        Map<String, String> cookieMap = getCookieMap(request);

        String refresh = cookieMap.get(REFRESH_TOKEN);
        String access = cookieMap.get(ACCESS_TOKEN);

        try {
            if (!(authService.validateToken(refresh) && authService.validateToken(access))) {
                return;
            }
            refreshTokenRepository.existsByRefresh(refresh).orElseThrow(NotFoundRefreshTokenException::new);
        } catch (CustomException e) {
            request.setAttribute("jwt exception", e);
        }

        refreshTokenRepository.deleteByRefresh(refresh);

        clearCookie(response, REFRESH_TOKEN);
        clearCookie(response, ACCESS_TOKEN);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private static Map<String, String> getCookieMap(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies()).stream().flatMap(Arrays::stream)
                .filter(cookie -> "refresh".equals(cookie.getName()) || "access".equals(cookie.getName()))
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
    }

    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
