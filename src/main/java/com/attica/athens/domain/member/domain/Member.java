package com.attica.athens.domain.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseMember {

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private Member(String username, String password) {
        super(MemberRole.ROLE_USER);
        this.username = username;
        this.password = password;
    }

    public static Member createUser(String username, String password) {
        return new Member(username, password);
    }
}
