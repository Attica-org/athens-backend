package com.attica.athens.global.auth.config.oauth2.member;

import java.util.Map;

/**
 * 구글 OAuth2 사용자 정보
 */
public class GoogleOAuth2MemberInfo extends OAuth2MemberInfo {
    public GoogleOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }
}
