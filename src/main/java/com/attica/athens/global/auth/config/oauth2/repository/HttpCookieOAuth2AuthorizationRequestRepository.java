package com.attica.athens.global.auth.config.oauth2.repository;

import static com.attica.athens.global.utils.CookieUtils.addCookie;
import static com.attica.athens.global.utils.CookieUtils.deleteCookie;
import static com.attica.athens.global.utils.CookieUtils.getCookie;
import static com.attica.athens.global.utils.SerializationUtils.deserialize;
import static com.attica.athens.global.utils.SerializationUtils.serialize;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

/**
 * OAuth2 인증 요청을 HTTP 쿠키에 저장하고 관리하는 Repository 클래스
 * <p>
 * OAuth2 인증 과정에서 상태를 유지하는 데 사용된다.
 * <p>
 * OAuth2 인증 프로세스에서 Spring Security OAuth2 클라이언트에 의해 호출된다.
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int cookieExpireSeconds = 180;

    /**
     * 로그인 수행시, OAuth2 인증 요청을 HTTP 쿠키에 저장한다. 리다이렉트 URI도 함께 저장한다.
     *
     * @param authorizationRequest 저장할 OAuth2 인증 요청
     * @param request              HTTP 요청
     * @param response             HTTP 응답
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                serialize(authorizationRequest), cookieExpireSeconds);

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds);
        }
    }

    /**
     * 로그인 완료 후 우리 서비스로 복귀할 때 사용한다. 쿠키로부터 HTTP 요청에서 저장했던 OAuth2 인증 요청을 로드한다.
     *
     * @param request HTTP 요청
     * @return 저장된 OAuth2 인증 요청, 없으면 null
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> deserialize(cookie.getValue(), OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * 인증이 끝났으니 사용했던 쿠키들을 제거한다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @return 저장된 OAuth2 인증 요청, 없으면 null
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    /**
     * OAuth2 인증 요청과 관련된 쿠키들을 제거한다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }

    /**
     * 저장된 리다이렉트 URI를 가져온다.
     *
     * @param request HTTP 요청
     * @return 저장된 리다이렉트 URI, 없으면 null
     */
    public String getRedirectUriAfterLogin(HttpServletRequest request) {
        return getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(cookie -> cookie.getValue())
                .orElse(null);
    }
}
