package com.attica.athens.global.auth.application;

import static com.attica.athens.global.auth.jwt.Constants.ACCESS_TOKEN;
import static com.attica.athens.global.auth.jwt.Constants.COOKIE_EXPIRATION_TIME;
import static com.attica.athens.global.auth.jwt.Constants.COOKIE_NAME;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;

import com.attica.athens.global.auth.CustomUserDetails;
import com.attica.athens.global.auth.dao.RefreshTokenRepository;
import com.attica.athens.global.auth.domain.RefreshToken;
import com.attica.athens.global.auth.dto.request.CreateRefreshTokenRequest;
import com.attica.athens.global.auth.exception.JwtExpiredException;
import com.attica.athens.global.auth.exception.JwtIllegalArgumentException;
import com.attica.athens.global.auth.exception.JwtSignatureException;
import com.attica.athens.global.auth.exception.JwtUnsupportedJwtException;
import com.attica.athens.global.auth.exception.NotFoundRefreshTokenException;
import com.attica.athens.global.auth.jwt.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createJwtToken(String tokenType, long id, String role) {
        return jwtUtils.createJwtToken(tokenType, id, role);
    }

    public boolean validateToken(String token) {
        try {
            jwtUtils.getClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtSignatureException();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new JwtUnsupportedJwtException();
        } catch (IllegalArgumentException e) {
            throw new JwtIllegalArgumentException();
        }
    }

    public Authentication createAuthenticationByToken(String token) {
        Long userId = Long.parseLong(jwtUtils.getUserId(token));
        String role = jwtUtils.getRole(token);

        CustomUserDetails customUserDetails = new CustomUserDetails(userId, "fakePassword", role);

        return new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
    }

    public void createRefreshEntity(CreateRefreshTokenRequest createRefreshTokenRequest) {
        Long userId = createRefreshTokenRequest.userId();
        String refresh = createRefreshTokenRequest.refresh();

        Date expiration = jwtUtils.getClaims(createRefreshTokenRequest.refresh()).getExpiration();

        RefreshToken refreshEntity = RefreshToken.createRefreshToken(userId, refresh, expiration);

        refreshTokenRepository.save(refreshEntity);
    }

    public String reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = getRefreshToken(request);

        validateToken(refreshToken);

        Long userId = Long.parseLong(jwtUtils.getUserId(refreshToken));
        String role = jwtUtils.getRole(refreshToken);
        String newAccess = createRefreshToken(userId, role, response);
        refreshTokenRepository.deleteByRefresh(refreshToken);
        return newAccess;
    }

    public String createRefreshTokenAndGetAccessToken(Long userId, String role, HttpServletResponse response) {
        return createRefreshToken(userId, role, response);
    }

    private String createRefreshToken(Long userId, String role, HttpServletResponse response) {
        String newAccess = jwtUtils.createJwtToken(ACCESS_TOKEN, userId, role);
        String newRefresh = jwtUtils.createJwtToken(REFRESH_TOKEN, userId, role);

        createRefreshEntity(new CreateRefreshTokenRequest(userId, newRefresh));

        Cookie cookie = createCookie(COOKIE_NAME, newRefresh);
        response.addCookie(cookie);

        return newAccess;
    }

    private String getRefreshToken(HttpServletRequest request) {

        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(NotFoundRefreshTokenException::new);
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
