package com.attica.athens.global.auth.jwt;

import static com.attica.athens.global.auth.jwt.Constants.ACCESS_TOKEN;
import static com.attica.athens.global.auth.jwt.Constants.AUTHORITY_KEY;
import static com.attica.athens.global.auth.jwt.Constants.AUTHORITY_ROLE;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JwtUtils(
            @Value("${auth.jwt.secret-key}") String secret,
            @Value("${auth.jwt.access-expired}") long accessTokenExpirationTime,
            @Value("${auth.jwt.refresh-expired}") long refreshTokenExpirationTime
    ) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwtToken(String tokenType, Long id, String role) {
        Date now = new Date();
        Date expire;

        if (REFRESH_TOKEN.equals(tokenType)) {
            expire = new Date(now.getTime() + refreshTokenExpirationTime);
            return Jwts.builder()
                    .claim(AUTHORITY_KEY, String.valueOf(id))
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
}
