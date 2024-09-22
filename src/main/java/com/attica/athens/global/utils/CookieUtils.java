package com.attica.athens.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

/**
 * HTTP 쿠키를 다루기 위한 유틸리티 클래스
 */
public class CookieUtils {

    /**
     * HTTP 요청에서 이름으로 쿠키를 검색한다.
     *
     * @param request HTTP 요청
     * @param name    쿠키의 이름
     * @return Optional<Cookie>
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    /**
     * 응답에 새 쿠키를 추가한다.
     *
     * @param response HTTP 응답
     * @param name     쿠키의 이름
     * @param value    쿠키의 값
     * @param maxAge   쿠키의 최대 수명 (초)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 이름으로 쿠키를 삭제한다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param name     삭제할 쿠키의 이름
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
