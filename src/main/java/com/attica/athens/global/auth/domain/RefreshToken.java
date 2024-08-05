package com.attica.athens.global.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
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
    private LocalDateTime expiration;

    @Builder
    private RefreshToken(Long userId, String refresh, LocalDateTime expiration) {
        this.userId = userId;
        this.refresh = refresh;
        this.expiration = expiration;
    }

    public static RefreshToken createRefreshToken(Long userId, String refresh, LocalDateTime date) {
        return new RefreshToken(userId, refresh, date);
    }
}