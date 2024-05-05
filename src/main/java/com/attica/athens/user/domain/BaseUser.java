package com.attica.athens.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class BaseUser {

    @Id
    private String id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private UserRole role;

    protected BaseUser(String id, UserRole role) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.role = role;
    }
}
