package com.attica.athens.global.security;

import com.attica.athens.domain.user.domain.TempUser;
import com.attica.athens.domain.user.domain.User;
import com.attica.athens.domain.user.domain.UserRole;
import com.attica.athens.global.config.SecurityConfig;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isNonProtectedUrl(request)) {
            filterChain.doFilter(request, response);

            return;
        }

        String token = resolveToken(request);

        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);

            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        String id = jwtUtil.getId(token);
        String role = jwtUtil.getRole(token);

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

    private String resolveToken(HttpServletRequest request) {

        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null || !authorization.startsWith(BEARER)) {
            throw new AuthenticationCredentialsNotFoundException("Token not found");
        }

        return authorization.split(" ")[1];
    }

    private Authentication createAuthentication(String id, String role) {

        CustomUserDetails customUserDetails =
                role.equals(UserRole.ROLE_TEMP_USER.name())
                        ? new CustomUserDetails(TempUser.createTempUser())
                        : new CustomUserDetails(User.createUser("fakeUsername", "fakePassword"));

        return new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
    }
}
