package com.attica.athens.global.auth.application;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.Member;
import com.attica.athens.global.auth.config.oauth2.OAuth2MemberInfoFactory;
import com.attica.athens.global.auth.config.oauth2.member.OAuth2MemberInfo;
import com.attica.athens.global.auth.domain.AuthProvider;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.global.auth.exception.OAuthProviderMissMatchException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 사용자 인증을 위한 커스텀 서비스 클래스 <p> OAuth2 인증 과정에서 사용자 정보를 로드하고 처리하는 역할을 한다.
 *
 * <p>이 서비스는 {@link DefaultOAuth2UserService}를 확장하여
 * OAuth2 인증 후 사용자 정보를 데이터베이스와 동기화하고, 커스텀 {@link CustomUserDetails} 객체를 생성한다.</p>
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    /**
     * OAuth2UserRequest 객체를 통해 OAuth2 인증 후 사용자 정보를 로드하고 처리한다.
     *
     * @param userRequest OAuth2 인증 요청 객체
     * @return OAuth2User 인터페이스를 구현한 사용자 정보 객체
     * @throws OAuth2AuthenticationException          OAuth2 인증 예외
     * @throws InternalAuthenticationServiceException 내부 인증 서비스 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * OAuth2 사용자 정보를 처리하고 데이터베이스와 동기화한다.
     *
     * <p>이 메소드는 OAuth2 제공자로부터 받은 사용자 정보를 처리하고, 해당 정보를 데이터베이스의 회원 정보와 동기화한다.
     * 새로운 사용자인 경우 회원 정보를 생성하고, 기존 사용자인 경우 정보를 확인한다.</p>
     *
     * @param userRequest OAuth2 사용자 요청 정보
     * @param member      OAuth2 인증으로 받아온 사용자 정보
     * @return 처리된 OAuth2User 객체
     * @throws OAuthProviderMissMatchException OAuth 제공자 불일치 시
     */
    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User member) {
        AuthProvider authProvider = AuthProvider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2MemberInfo memberInfo = OAuth2MemberInfoFactory.getOAuth2MemberInfo(authProvider, member.getAttributes());
        Optional<Member> optionalMember = memberRepository.findByOauthId(memberInfo.getId());

        Member savedMember = optionalMember.map(existingMember -> {
            if (authProvider != existingMember.getAuthProvider()) {
                throw new OAuthProviderMissMatchException();
            }
            return existingMember;
        }).orElseGet(() -> memberRepository.save(Member.createMemberWithOAuthInfo(memberInfo, authProvider)));

        return CustomUserDetails.create(savedMember, member.getAttributes());
    }
}
