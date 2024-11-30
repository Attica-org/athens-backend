package com.attica.athens.global.auth.config.oauth2.member;

import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 인증을 통해 얻은 회원 정보를 추상화한 클래스 이 클래스는 다양한 OAuth2 제공자로부터 얻은 회원 정보를 일관된 방식으로 처리하기 위한 기본 구조를 제공한다.
 *
 * <p>이 클래스를 상속받는 구체적인 구현체들은 각 OAuth2 제공자의 특성에 맞게 {@link #getId()} 메소드를 구현해야 한다.</p>
 */
public abstract class OAuth2MemberInfo {

    protected Map<String, Object> attributes;

    public OAuth2MemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract Optional<String> getEmail();
}
