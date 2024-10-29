package com.attica.athens.global.auth.application;

import static com.attica.athens.global.auth.jwt.Constants.COOKIE_EXPIRATION_TIME;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;
import static java.lang.Boolean.TRUE;

import com.attica.athens.domain.member.exception.InvalidTempTokenException;
import com.attica.athens.global.auth.config.properties.AppProperties;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.global.auth.exception.JwtExpiredException;
import com.attica.athens.global.auth.exception.JwtIllegalArgumentException;
import com.attica.athens.global.auth.exception.JwtSignatureException;
import com.attica.athens.global.auth.exception.JwtUnsupportedJwtException;
import com.attica.athens.global.auth.exception.NotFoundRefreshTokenException;
import com.attica.athens.global.auth.jwt.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String BLACKLIST_ACCESS_PREFIX = "blacklist:access:";
    private static final String BLACKLIST_REFRESH_PREFIX = "blacklist:refresh:";
    private static final String TEMP_TOKEN_PREFIX = "temp:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    public static final String LOGOUT = "logout";

    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final AppProperties appProperties;

    public AuthService(final JwtUtils jwtUtils,
                       @Qualifier("redisTemplate") final RedisTemplate<String, String> redisTemplate,
                       final AppProperties appProperties) {
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.appProperties = appProperties;
    }

    public String getAccessToken(String tempToken) {
        String redisKey = TEMP_TOKEN_PREFIX + tempToken;
        String accessToken = redisTemplate.opsForValue().get(redisKey);

        if (accessToken == null) {
            throw new InvalidTempTokenException();
        }
        redisTemplate.delete(tempToken);

        return accessToken;
    }

    public void saveTempToken(String tempToken, String accessToken) {
        String redisKey = TEMP_TOKEN_PREFIX + tempToken;
        redisTemplate.opsForValue()
                .set(redisKey, accessToken,
                        appProperties.getAuth().getTempToken().getExpirationMinutes(), TimeUnit.MILLISECONDS);
    }

    public void saveBlacklistAccessToken(String accessToken, Long expirationTime) {
        String redisKey = BLACKLIST_ACCESS_PREFIX + accessToken;
        redisTemplate.opsForValue()
                .setIfAbsent(redisKey, LOGOUT, Duration.ofMillis(expirationTime - System.currentTimeMillis()));
    }

    public void saveBlacklistRefreshToken(String refreshToken, Long expirationTime) {
        String redisKey = BLACKLIST_REFRESH_PREFIX + refreshToken;
        redisTemplate.opsForValue()
                .setIfAbsent(redisKey, LOGOUT, Duration.ofMillis(expirationTime - System.currentTimeMillis()));
    }

    public boolean isBlacklistAccessToken(String token) {
        String redisKey = BLACKLIST_ACCESS_PREFIX + token;
        return TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    public void saveRefreshToken(Long userId, String refreshToken) {
        String redisKey = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue()
                .set(redisKey, userId.toString(), appProperties.getAuth().getJwt().getRefreshExpired(),
                        TimeUnit.MILLISECONDS);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
    }

    public String getRefreshToken(String refreshToken) {
        String redisKey = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.opsForValue()
                .get(redisKey);
    }

    public String createAccessToken(Long id, String role) {
        return jwtUtils.createAccessToken(id, role);
    }

    public String createRefreshToken(Long id, String role) {
        return jwtUtils.createRefreshToken(id, role);
    }

    public boolean validateToken(String token) {
        try {
            jwtUtils.getClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtSignatureException();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new JwtUnsupportedJwtException();
        } catch (IllegalArgumentException e) {
            throw new JwtIllegalArgumentException();
        }
        return true;
    }

    public Authentication createAuthenticationByToken(String token) {
        Long userId = jwtUtils.getUserId(token);
        String role = jwtUtils.getRole(token);

        CustomUserDetails customUserDetails = new CustomUserDetails(userId, "fakePassword", role);

        return new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
    }

    public String reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = getRefreshToken(request);

        validateToken(refreshToken);

        Long userId = jwtUtils.getUserId(refreshToken);
        String role = jwtUtils.getRole(refreshToken);

        return createRefreshTokenAndGetAccessToken(userId, role, response);
    }

    public String createRefreshTokenAndGetAccessToken(Long userId, String role, HttpServletResponse response) {
        String newAccess = jwtUtils.createAccessToken(userId, role);
        String newRefresh = jwtUtils.createRefreshToken(userId, role);

        Cookie cookie = createCookie(REFRESH_TOKEN, newRefresh);
        addSameSiteCookieAttribute(response, cookie);

        return newAccess;
    }

    private String getRefreshToken(HttpServletRequest request) {

        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(NotFoundRefreshTokenException::new);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(COOKIE_EXPIRATION_TIME);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addSameSiteCookieAttribute(HttpServletResponse response, Cookie cookie) {
        String cookieString = String.format("%s=%s; Max-Age=%d; Path=%s; Secure; HttpOnly; SameSite=None",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge(),
                cookie.getPath());

        response.addHeader("Set-Cookie", cookieString);
    }

    public long getExpirationTime(final String accessToken) {
        return jwtUtils.getExpirationTimeInMillis(accessToken);
    }
}
