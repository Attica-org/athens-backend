package com.attica.athens.global.auth.config.oauth2.handler;

import static com.attica.athens.global.auth.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.attica.athens.global.utils.CookieUtils.getCookie;

import com.attica.athens.global.auth.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2 인증 실패 시 호출되는 핸들러 클래스
 *
 * <p>인증 실패 시, 사용자를 적절한 URI로 리다이렉트한다. </p>
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    /**
     * OAuth2 인증 실패 시 호출되는 메서드
     *
     * <p>이 메서드는 인증 실패를 처리하고 사용자를 적절한 URL로 리다이렉트한다.</p>
     *
     * @param request   현재 HTTP 요청
     * @param response  HTTP 응답
     * @param exception 인증 실패를 나타내는 예외
     * @throws IOException 입출력 예외
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String targetUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        exception.printStackTrace();

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
