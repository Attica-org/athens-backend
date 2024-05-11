package com.attica.athens.domain.user.domain;

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

    private String username;

    private String password;

    public User(String id, String username, String password) {
        super(UserRole.ROLE_USER);
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
