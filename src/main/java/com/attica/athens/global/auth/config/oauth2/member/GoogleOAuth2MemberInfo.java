package com.attica.athens.global.auth.config.oauth2.member;

import java.util.Map;
import java.util.Optional;

/**
 * 구글 OAuth2 사용자 정보
 */
public class GoogleOAuth2MemberInfo extends OAuth2MemberInfo {

    private static final String ID = "sub";
    private static final String EMAIL = "email";

    public GoogleOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get(ID);
    }

    @Override
    public Optional<String> getEmail() {
        return Optional.ofNullable((String) attributes.get(EMAIL));
    }
}
