package com.attica.athens.user.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Getter
public class User extends BaseUser {

    private String username;

    private String password;

    public User(String id, String username, String password) {
        super(id, UserRole.ROLE_USER);
        this.username = username;
        this.password = password;
    }
}
