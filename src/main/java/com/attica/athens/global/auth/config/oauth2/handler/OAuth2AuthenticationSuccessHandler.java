package com.attica.athens.global.auth.config.oauth2.handler;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.exception.NotFoundOAuthMemberException;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.attica.athens.global.auth.config.properties.AppProperties;
import com.attica.athens.global.auth.domain.AuthProvider;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2 인증 성공 시 호출되는 핸들러 클래스
 * <p>
 * 인증 성공 후 사용자를 적절한 URI로 리다이렉트하고, 인증 관련 쿠키를 정리한다. 프론트엔드 도메인 내의 모든 경로로의 리다이렉트를 허용하며, 허용되지 않은 도메인으로의 리다이렉트 시도를 안전하게 처리한다.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String SUB = "sub";
    public static final String ID = "id";
    private static final Map<String, String> PROVIDER_ID_ATTRIBUTE_MAP = Map.of(
            AuthProvider.GOOGLE.getProviderName(), SUB,
            AuthProvider.KAKAO.getProviderName(), ID
    );

    private final AuthService authService;
    private final AppProperties appProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final MemberRepository memberRepository;

    /**
     * OAuth2AuthenticationSuccessHandler 생성자
     *
     * @param authService   인증 서비스
     * @param appProperties 애플리케이션 속성
     * @param redisTemplate Redis 템플릿
     */
    public OAuth2AuthenticationSuccessHandler(final AuthService authService, final AppProperties appProperties,
                                              @Qualifier("redisTemplate") final RedisTemplate<String, String> redisTemplate,
                                              final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
                                              final MemberRepository memberRepository) {
        this.authService = authService;
        this.appProperties = appProperties;
        this.redisTemplate = redisTemplate;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 인증 성공 시 호출되는 메서드
     * <p>
     * 사용자의 권한 정보를 확인하고, 새로운 액세스 토큰을 생성한다. 임시 토큰을 생성하여 Redis에 저장하고, 리다이렉트할 URL을 결정한다.
     *
     * @param request        HTTP 요청
     * @param response       HTTP 응답
     * @param authentication 인증 정보
     * @throws IOException IO 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect.");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        String oauthId = extractOauthId(oauth2User, registrationId);
        Long memberId = memberRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new NotFoundOAuthMemberException(oauthId))
                .getId();

        String authority = extractAuthority(authentication);

        String accessToken = authService.createRefreshTokenAndGetAccessToken(memberId, authority, response);

        String tempToken = UUID.randomUUID()
                .toString();
        authService.saveTempToken(tempToken, accessToken);

        clearAuthenticationAttributes(request);

        String targetUrl = determineTargetUrl(request, tempToken);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * OAuth2 인증 공급자(OAuth2User)로부터 사용자 ID를 추출한다.
     *
     * @param oauth2User     OAuth2 사용자
     * @param registrationId OAuth2 공급자 ID
     * @return 사용자 ID
     */

    private String extractOauthId(OAuth2User oauth2User, String registrationId) {
        String userIdAttributeName = PROVIDER_ID_ATTRIBUTE_MAP.get(registrationId);
        if (userIdAttributeName == null) {
            throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        }

        Object userIdAttribute = oauth2User.getAttribute(userIdAttributeName);
        if (userIdAttribute == null) {
            throw new IllegalArgumentException("User ID attribute not found: " + userIdAttributeName);
        }
        return userIdAttribute.toString();
    }

    /**
     * 인증 정보로부터 권한을 추출한다.
     *
     * @param authentication 인증 정보
     * @return 권한
     */

    private String extractAuthority(Authentication authentication) {
        return authentication.getAuthorities().iterator().next().getAuthority();
    }

    /**
     * 리다이렉트할 URL을 결정한다.
     * <p>
     * 쿠키에 저장된 리다이렉트 URI를 확인하고, 해당 URI가 허용된 것인지 검증한다. 허용되지 않은 URI인 경우 기본 리다이렉트 URI를 사용한다. 임시 토큰을 URI에 추가하여 리다이렉트 URL을
     * 생성한다.
     *
     * @param request   HTTP 요청
     * @param tempToken 임시 토큰
     * @return 결정된 리다이렉트 URL
     */
    protected String determineTargetUrl(HttpServletRequest request, String tempToken) {
        String redirectUri = httpCookieOAuth2AuthorizationRequestRepository.getRedirectUriAfterLogin(request);

        if (StringUtils.isBlank(redirectUri) || !isAuthorizedRedirectUri(redirectUri)) {
            redirectUri = appProperties.getOauth2().getDefaultRedirectUri();
        }

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("temp-token", tempToken)
                .build()
                .toUriString();
    }

    /**
     * 주어진 URI가 허용된 리다이렉트 URI인지 검증한다.
     * <p>
     * 기본 도메인의 호스트와 포트가 일치하기만 한다면, 프론트엔드 도메인의 모든 경로를 허용한다.
     *
     * @param uri 검증할 URI
     * @return 허용된 URI이면 true, 그렇지 않으면 false
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return appProperties.getOauth2()
                .getRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);

                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
