package com.attica.athens.global.auth.domain;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshToken_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String refresh;

    @Column(nullable = false)
    private Date expiration;

    private RefreshToken(Long userId, String refresh, Date expiration) {
        this.userId = userId;
        this.refresh = refresh;
        this.expiration = expiration;
    }

    public static RefreshToken createRefreshToken(Long userId, String refresh, Date date) {
        return new RefreshToken(userId, refresh, date);
    }
}