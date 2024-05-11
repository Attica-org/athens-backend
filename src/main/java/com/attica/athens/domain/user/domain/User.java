package com.attica.athens.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUser {

    @Id
    private String id;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private User(String id, String username, String password) {
        super(UserRole.ROLE_USER);
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static User of(String id, String username, String password) {
        return new User(id, username, password);
    }
}
