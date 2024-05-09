package com.attica.athens.agora.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CategoryName {
    CT_0001("IT")
    ;

    private final String code;

    public String code() {
        return code;
    }
}
