package com.attica.athens.global.utils;

import java.util.Base64;

/**
 * 객체 직렬화와 역직렬화를 수행하는 유틸리티 클래스
 */
public class SerializationUtils {

    /**
     * 객체를 Base64 인코딩된 문자열로 직렬화한다.
     *
     * @param object 직렬화할 객체
     * @return Base64 인코딩된 문자열
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(org.springframework.util.SerializationUtils.serialize(object));
    }

    /**
     * Base64 인코딩된 문자열을 객체로 역직렬화한다.
     *
     * @param serialized Base64 인코딩된 직렬화 문자열
     * @param cls        역직렬화할 객체의 클래스
     * @return 역직렬화된 객체
     */
    public static <T> T deserialize(String serialized, Class<T> cls) {
        return cls.cast(org.springframework.util.SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(serialized)));
    }
}
