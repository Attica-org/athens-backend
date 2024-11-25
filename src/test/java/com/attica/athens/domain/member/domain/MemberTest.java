package com.attica.athens.domain.member.domain;

import static com.attica.athens.domain.member.domain.Member.createMember;
import static com.attica.athens.domain.member.domain.Member.createMemberWithOAuthInfo;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.attica.athens.global.auth.config.oauth2.member.GoogleOAuth2MemberInfo;
import com.attica.athens.global.auth.config.oauth2.member.KakaoOAuth2MemberInfo;
import com.attica.athens.global.auth.domain.AuthProvider;
import com.attica.athens.global.auth.exception.NullFieldException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("멤버 테스트")
class MemberTest {

    @Nested
    @DisplayName("멤버 생성 테스트")
    public class MemberCreationTest {

        @Test
        @DisplayName("유효한 파라미터가 주어지면 멤버가 생성된다")
        void 성공_멤버생성_유효한파라미터() {
            // given
            String username = "testUser";
            String password = "testPassword";
            AuthProvider provider = AuthProvider.LOCAL;
            String oauthId = "testOauthId";

            // when
            Member member = createMember(username, password, provider, oauthId);

            // then
            then(member.getUsername()).isEqualTo(username);
            then(member.getPassword()).isEqualTo(password);
            then(member.getAuthProvider()).isEqualTo(provider);
            then(member.getOauthId()).isEqualTo(oauthId);
        }

        @Test
        @DisplayName("username이 null이면 예외가 발생한다")
        void 실패_멤버생성_username이null() {
            // given
            String username = null;
            String password = "testPassword";
            AuthProvider provider = AuthProvider.LOCAL;
            String oauthId = "testOauthId";

            // when, then
            assertNullFieldException(username, password, provider, oauthId, "username");
        }

        @Test
        @DisplayName("username이 null이면 예외가 발생한다")
        void 실패_멤버생성_password이null() {
            // given
            String username = "testUser";
            String password = null;
            AuthProvider provider = AuthProvider.LOCAL;
            String oauthId = "testOauthId";

            // when, then
            assertNullFieldException(username, password, provider, oauthId, "password");
        }

        @Test
        @DisplayName("provider이 null이면 예외가 발생한다")
        void 실패_멤버생성_provider이null() {
            // given
            String username = "testUser";
            String password = "testPassword";
            AuthProvider provider = null;
            String oauthId = "testOauthId";

            // when, then
            assertNullFieldException(username, password, provider, oauthId, "authProvider");
        }
    }

    private void assertNullFieldException(final String username, final String password,
                                          final AuthProvider provider,
                                          final String oauthId, final String field) {
        thenThrownBy(() -> createMember(username, password, provider, oauthId))
                .isInstanceOf(NullFieldException.class)
                .hasMessage("The field " + field + " must not be null");
    }

    @Nested
    @DisplayName("OAuth 정보로 멤버 생성 테스트")
    public class MemberCreationWithOauthInfoTest {

        @Test
        @DisplayName("Kakao Oauth 정보로 멤버 생성한다")
        void 성공_멤버생성_KakaoOauth정보() {
            // given
            String oauthId = "testOauthId";
            String nickname = "testUser";
            KakaoOAuth2MemberInfo kakaoOAuth2MemberInfo = new KakaoOAuth2MemberInfo(Map.of(
                    "id", oauthId,
                    "properties", Map.of(
                            "nickname", nickname
                    ))
            );

            // when
            Member member = createMemberWithOAuthInfo(kakaoOAuth2MemberInfo, AuthProvider.KAKAO);

            // then
            then(member.getUsername()).isEqualTo("DEFAULT");
            then(member.getPassword()).isEqualTo("NOPASSWORD");
            then(member.getAuthProvider()).isEqualTo(AuthProvider.KAKAO);
            then(member.getOauthId()).isEqualTo(oauthId);
        }

        @Test
        @DisplayName("Google Oauth 정보로 멤버 생성한다")
        void 성공_멤버생성_GoogleOauth정보() {
            // given
            String oauthId = "testOauthId";
            String email = "testUser@gmail.com";
            GoogleOAuth2MemberInfo googleOAuth2MemberInfo = new GoogleOAuth2MemberInfo(Map.of(
                    "sub", oauthId,
                    "email", email)
            );

            // when
            Member member = createMemberWithOAuthInfo(googleOAuth2MemberInfo, AuthProvider.GOOGLE);

            // then
            then(member.getUsername()).isEqualTo("DEFAULT");
            then(member.getPassword()).isEqualTo("NOPASSWORD");
            then(member.getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
            then(member.getOauthId()).isEqualTo(oauthId);
            then(member.getEmail()).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("멤버 정보 갱신 테스트")
    class MemberUpdateTest {

        @Test
        @DisplayName("카카오 OAuth 멤버 정보를 갱신한다")
        void 성공_멤버갱신_KakaoOauth정보() {
            // given
            String kakaoAccountId = "kakao123456789";
            String initialEmail = "kakao@hanmail.net";
            KakaoOAuth2MemberInfo initialKakaoInfo = new KakaoOAuth2MemberInfo(Map.of(
                    "id", kakaoAccountId,
                    "kakao_account", Map.of(
                            "email", initialEmail
                    ))
            );
            Member kakaoMember = createMemberWithOAuthInfo(initialKakaoInfo, AuthProvider.KAKAO);

            String updatedEmail = "kakao2@hanmail.net";
            KakaoOAuth2MemberInfo updatedKakaoInfo = new KakaoOAuth2MemberInfo(Map.of(
                    "id", kakaoAccountId,
                    "kakao_account", Map.of(
                            "email", updatedEmail
                    ))
            );

            // when
            kakaoMember.updateMemberInfo(updatedKakaoInfo);

            // then
            then(kakaoMember.getEmail()).isEqualTo(updatedEmail);
        }

        @Test
        @DisplayName("구글 OAuth 멤버 정보를 갱신한다")
        void 성공_멤버갱신_GoogleOauth정보() {
            // given
            String googleAccountId = "google123456789";
            String initialEmail = "google1@gmail.com";
            GoogleOAuth2MemberInfo initialGoogleInfo = new GoogleOAuth2MemberInfo(Map.of(
                    "sub", googleAccountId,
                    "email", initialEmail)
            );
            Member googleMember = createMemberWithOAuthInfo(initialGoogleInfo, AuthProvider.KAKAO);

            String updatedEmail = "google2@gmail.com";
            GoogleOAuth2MemberInfo updatedGoogleInfo = new GoogleOAuth2MemberInfo(Map.of(
                    "sub", googleAccountId,
                    "email", updatedEmail)
            );
            // when
            googleMember.updateMemberInfo(updatedGoogleInfo);

            // then
            then(googleMember.getEmail()).isEqualTo(updatedEmail);
        }
    }
}
