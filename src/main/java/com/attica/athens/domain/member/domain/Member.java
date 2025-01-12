package com.attica.athens.domain.member.domain;

import com.attica.athens.global.auth.config.oauth2.member.OAuth2MemberInfo;
import com.attica.athens.global.auth.domain.AuthProvider;
import com.attica.athens.global.auth.exception.NullFieldException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "auth_provider"})
})
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

    @Column(length = 254)
    @Size(max = 254)
    private String email;

    private Member(@NotNull String username,
                   @NotNull String password,
                   @NotNull AuthProvider authProvider,
                   String oauthId,
                   String email) {
        super(MemberRole.ROLE_USER);

        validateUsername(username);
        validatePassword(password);
        validateAuthProvider(authProvider);

        this.username = username;
        this.password = password;
        this.authProvider = authProvider;
        this.oauthId = oauthId;
        this.email = email;
    }

    private void validateUsername(String username) {
        if (username == null) {
            throw new NullFieldException("username");
        }
    }

    private void validatePassword(String password) {
        if (password == null) {
            throw new NullFieldException("password");
        }
    }

    private void validateAuthProvider(AuthProvider authProvider) {
        if (authProvider == null) {
            throw new NullFieldException("authProvider");
        }
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
        return new Member(username, password, authProvider, oauthId, null);
    }

    public static Member createMemberWithOAuthInfo(OAuth2MemberInfo memberInfo, AuthProvider authProvider) {
        return new Member("DEFAULT", "NOPASSWORD", authProvider, memberInfo.getId(),
                memberInfo.getEmail().orElse(null));
    }

    public void updateMemberInfo(OAuth2MemberInfo memberInfo) {
        this.email = memberInfo.getEmail().orElse(null);
    }
}
