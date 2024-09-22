package com.attica.athens.global.auth.domain;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE,
    KAKAO,
    LOCAL;

    public String getProviderName() {
        return name().toLowerCase();
    }
}
