package com.attica.athens.global.auth.config.oauth2.member;

import java.util.Map;

/**
 * 카카오 OAuth2 사용자 정보
 */
public class KakaoOAuth2MemberInfo extends OAuth2MemberInfo {
    public KakaoOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }
}
