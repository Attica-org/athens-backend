package com.attica.athens.domain.token.domain;

import com.attica.athens.domain.common.AuditingFields;
import com.attica.athens.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshToken_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(nullable = false)
    private String refresh;

    private RefreshToken(String username, String refresh) {
        this.username = username;
        this.refresh = refresh;
    }
    public static RefreshToken createRefreshToken(String username, String refresh) {
        return new RefreshToken(username, refresh);
    }
}
