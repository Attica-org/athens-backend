package com.attica.athens.domain.common.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_FAILED(1001, "요청에 대한 유효성 검증 실패"),
    WRONG_REQUEST_TRANSMISSION(1002, "잘못된 요청의 전달"),
    MISSING_PART(1003, "필수적인 요소의 누락"),
    DUPLICATE_RESOURCE(1004, "중복된 리소스의 전달"),
    ROLE_BASED_ACCESS_ERROR(1101, "역할 기반 접근 제어 오류"),
    RESOURCE_ACCESS_FORBIDDEN(1102, "리소스 접근 권한 오류"),
    AUTHENTICATION_FAILED(1201, "인증 실패"),
    ACCESS_DENIED(1202, "리소스 접근에 대한 권한 부족"),
    RESOURCE_NOT_FOUND(1301, "존재하지 않는 리소스 접근 시도"),
    INTERNAL_SERVER_ERROR(2000, "서버 내부 오류");

    private final int code;
    private final String description;
}
