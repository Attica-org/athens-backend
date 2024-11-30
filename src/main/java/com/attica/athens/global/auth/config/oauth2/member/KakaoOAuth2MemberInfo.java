package com.attica.athens.global.auth.config.oauth2.member;

import java.util.Map;
import java.util.Optional;

/**
 * 카카오 OAuth2 사용자 정보
 */
public class KakaoOAuth2MemberInfo extends OAuth2MemberInfo {

    private static final String ID = "id";
    private static final String KAKAO_ACCOUNT = "kakao_account";
    private static final String EMAIL = "email";

    public KakaoOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get(ID).toString();
    }

    @Override
    public Optional<String> getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get(KAKAO_ACCOUNT);
        if (kakaoAccount == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((String) kakaoAccount.get(EMAIL));
    }
}
