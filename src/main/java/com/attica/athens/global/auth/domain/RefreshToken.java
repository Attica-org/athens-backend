package com.attica.athens.global.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String refresh;

    @Column(nullable = false)
    private LocalDateTime expiration;

    public RefreshToken(String userId, String refresh, LocalDateTime expiration) {
        this.userId = userId;
        this.refresh = refresh;
        this.expiration = expiration;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRefresh() {
        return refresh;
    }

    public LocalDateTime getExpirationDateTime() {
        return expiration;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}
