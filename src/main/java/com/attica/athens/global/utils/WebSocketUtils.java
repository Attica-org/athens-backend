package com.attica.athens.global.utils;

import java.util.Map;

public class WebSocketUtils {
    private static final ThreadLocal<Map<String, Object>> sessionAttributesHolder = new ThreadLocal<>();

    public static void setSessionAttributes(Map<String, Object> sessionAttributes) {
        sessionAttributesHolder.set(sessionAttributes);
    }

    public static Map<String, Object> getSessionAttributes() {
        return sessionAttributesHolder.get();
    }

    public static void removeSessionAttributes() {
        sessionAttributesHolder.remove();
    }
}
