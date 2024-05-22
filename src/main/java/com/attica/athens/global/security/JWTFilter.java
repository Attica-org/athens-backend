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

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("access");

        if (isNonProtectedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isExpired(accessToken)) {
            // 이후 전역 핸들러에서 응답
            throw new ExpiredJwtException(null, null, "Token has expired");
        }

        String category = jwtUtil.getCategory(accessToken);

        // 이후 CUSTOMEXCEPTION 작성
        if(!isAccessToken(category)){
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED,"Access 토큰이 아닙니다");
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
        return category.equals("access");
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
