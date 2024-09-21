package com.attica.athens.global.auth.jwt;

import static com.attica.athens.global.auth.jwt.Constants.ACCESS_TOKEN;
import static com.attica.athens.global.auth.jwt.Constants.AUTHORITY_KEY;
import static com.attica.athens.global.auth.jwt.Constants.AUTHORITY_ROLE;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;

import com.attica.athens.global.auth.config.properties.AppProperties;
import com.attica.athens.global.auth.config.properties.AppProperties.Auth.Jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final AppProperties appProperties;

    private SecretKey secretKey;
    private long accessTokenExpirationTime;
    private long refreshTokenExpirationTime;

    @PostConstruct
    public void init() {
        Jwt jwtProperties = appProperties.getAuth().getJwt();
        this.secretKey = new SecretKeySpec(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpirationTime = jwtProperties.getAccessExpired();
        this.refreshTokenExpirationTime = jwtProperties.getRefreshExpired();
    }

    public String createJwtToken(String tokenType, String id, String role) {
        Date now = new Date();
        Date expire;

        if (REFRESH_TOKEN.equals(tokenType)) {
            expire = new Date(now.getTime() + refreshTokenExpirationTime);
            return Jwts.builder()
                    .claim(AUTHORITY_KEY, id)
                    .claim(AUTHORITY_ROLE, role)
                    .issuedAt(now)
                    .expiration(expire)
                    .subject(REFRESH_TOKEN)
                    .signWith(secretKey)
                    .compact();
        }

        expire = new Date(now.getTime() + accessTokenExpirationTime);
        return Jwts.builder()
                .claim(AUTHORITY_KEY, String.valueOf(id))
                .claim(AUTHORITY_ROLE, role)
                .issuedAt(now)
                .expiration(expire)
                .subject(ACCESS_TOKEN)
                .signWith(secretKey)
                .compact();
    }

    public String getUserId(String token) { // secretKey로 검증
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(AUTHORITY_KEY, String.class);
        } catch (JwtException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(AUTHORITY_ROLE, String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public LocalDateTime getExpirationAsLocalDateTime(String token) {
        Date expirationDate = getClaims(token).getExpiration();
        return LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
    }
}
