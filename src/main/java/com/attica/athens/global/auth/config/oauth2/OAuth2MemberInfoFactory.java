package com.attica.athens.global.auth.config.oauth2;

import com.attica.athens.global.auth.config.oauth2.member.GoogleOAuth2MemberInfo;
import com.attica.athens.global.auth.config.oauth2.member.KakaoOAuth2MemberInfo;
import com.attica.athens.global.auth.config.oauth2.member.OAuth2MemberInfo;
import com.attica.athens.global.auth.domain.AuthProvider;
import com.attica.athens.global.auth.exception.UnsupportedProviderException;
import java.util.Map;

/**
 * OAuth2 회원 정보 객체를 생성하는 팩토리 클래스
 * <p> 이 클래스는 다양한 OAuth2 제공자에 대한 {@link OAuth2MemberInfo} 객체를 생성한다.
 */
public class OAuth2MemberInfoFactory {
    public static OAuth2MemberInfo getOAuth2MemberInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case KAKAO -> {
                return new KakaoOAuth2MemberInfo(attributes);
            }
            case GOOGLE -> {
                return new GoogleOAuth2MemberInfo(attributes);
            }
            default -> throw new UnsupportedProviderException();
        }
    }
}
