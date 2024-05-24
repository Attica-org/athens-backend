package com.attica.athens.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {


    private static SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm());
    }

    public static Long getId(String token) { // secretKey로 검증

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("id", Long.class);
    }

    public static String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("role", String.class);
    }

    public static Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

    public static String createJwt(String category, Long id, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("id", id)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }


    public static Authentication createAuthentication(Long id, String role) {

        CustomUserDetails customUserDetails = new CustomUserDetails(id, "fakePassword", role);

        return new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
    }
}
