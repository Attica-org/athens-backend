package com.attica.athens.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUser {

    @Column(length = 50, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private User(String username, String password) {
        super(UserRole.ROLE_USER);
        this.username = username;
        this.password = password;
    }

    public static User createUser(String username, String password) {
        return new User(username, password);
    }
}
