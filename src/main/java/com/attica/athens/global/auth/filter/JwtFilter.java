package com.attica.athens.global.auth.filter;

import static com.attica.athens.global.auth.jwt.Constants.BEARER_;
import static com.attica.athens.global.auth.jwt.Constants.REQUEST_ATTRIBUTE_NAME;

import com.attica.athens.domain.common.advice.CustomException;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.config.SecurityConfig;
import com.attica.athens.global.auth.exception.BlacklistedTokenException;
import com.attica.athens.global.auth.exception.InvalidAuthorizationHeaderException;
import com.attica.athens.global.auth.exception.InvalidRequestException;
import com.attica.athens.global.auth.jwt.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isNonProtectedUrl(request)) {
            filterChain.doFilter(request, response);

            return;
        }

        String header = request.getHeader(Constants.AUTHORIZATION);

        if (!Objects.equals(header, null)) {
            try {
                if (header.startsWith(BEARER_)) {
                    String token = header.split(" ")[1];
                    if (authService.isBlacklistAccessToken(token)) {
                        throw new BlacklistedTokenException();
                    }
                    authService.verifyToken(token);
                    Authentication authentication = authService.createAuthenticationByToken(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new InvalidAuthorizationHeaderException();
                }
            } catch (CustomException e) {
                request.setAttribute(REQUEST_ATTRIBUTE_NAME, e);
            }
        } else {
            request.setAttribute(REQUEST_ATTRIBUTE_NAME, new InvalidRequestException());
        }

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
