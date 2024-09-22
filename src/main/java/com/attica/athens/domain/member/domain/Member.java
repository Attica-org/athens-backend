package com.attica.athens.domain.member.domain;

import com.attica.athens.global.auth.config.oauth2.member.OAuth2MemberInfo;
import com.attica.athens.global.auth.domain.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseMember {

    @Column(length = 50, nullable = false, unique = true)
    @NotNull
    private String username;

    @Column(nullable = false)
    @NotNull
    private String password;

    @Column(name = "auth_provider", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AuthProvider authProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    private Member(@NotNull String username,
                   @NotNull String password,
                   @NotNull AuthProvider authProvider,
                   String oauthId) {
        super(MemberRole.ROLE_USER);
        this.username = username;
        this.password = password;
        this.authProvider = authProvider;
        this.oauthId = oauthId;
    }

    public static Member createMember(String username, String password, AuthProvider authProvider, String oauthId) {
        return new Member(username, password, authProvider, oauthId);
    }

    public static Member createMemberWithOAuthInfo(OAuth2MemberInfo memberInfo, AuthProvider authProvider) {
        return new Member("DEFAULT", "NOPASSWORD", authProvider, memberInfo.getId());
    }
}
