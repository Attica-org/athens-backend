package com.attica.athens.domain.member.domain;

import com.attica.athens.global.auth.config.oauth2.member.OAuth2MemberInfo;
import com.attica.athens.global.auth.domain.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseMember {

    @Column(length = 50, nullable = false)
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

    @Column(length = 100)
    @Size(max = 100)
    private String nickname;

    @Column(length = 254, unique = true)
    @Size(max = 254)
    private String email;

    private Member(@NotNull String username,
                   @NotNull String password,
                   @NotNull AuthProvider authProvider,
                   String oauthId,
                   String nickname,
                   String email) {
        super(MemberRole.ROLE_USER);
        this.username = username;
        this.password = password;
        this.authProvider = authProvider;
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.email = email;
    }

    /**
     * 기본 회원 생성 메서드
     *
     * @param username
     * @param password
     * @param authProvider
     * @param oauthId
     * @return
     */
    public static Member createMember(String username, String password, AuthProvider authProvider, String oauthId) {
        return new Member(username, password, authProvider, oauthId, null, null);
    }

    public static Member createMemberWithOAuthInfo(OAuth2MemberInfo memberInfo, AuthProvider authProvider) {
        return new Member("DEFAULT", "NOPASSWORD", authProvider, memberInfo.getId(),
                memberInfo.getNickname().orElse(null),
                memberInfo.getEmail().orElse(null));
    }

    public void updateOAuthInfo(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
