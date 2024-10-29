package com.attica.athens.global.auth.filter;

import static com.attica.athens.global.auth.jwt.Constants.COOKIE_EXPIRATION_TIME;
import static com.attica.athens.global.auth.jwt.Constants.REFRESH_TOKEN;
import static com.attica.athens.global.utils.CookieUtils.addCookie;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.global.auth.dto.response.CreateAccessTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 사용자 로그인 인증을 처리하는 필터 클래스 이 클래스는 {@link UsernamePasswordAuthenticationFilter}를 확장하여 사용자명과 비밀번호를 이용한 인증 프로세스를 구현한다.
 *
 * <p>인증 성공 시 JWT 토큰을 생성하고, 실패 시 적절한 응답을 반환한다.</p>
 */
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    /**
     * 사용자 인증을 시도한다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증 결과를 나타내는 {@link Authentication} 객체
     * @throws AuthenticationException 인증 실패 시 발생
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
                null);

        return authenticationManager.authenticate(authToken);
    }

    /**
     * 인증 성공 시 호출되는 메소드 JWT 토큰을 생성하고 응답에 포함시킨다.
     * refresh 토큰은 캐시에 저장한다.
     *
     * @param request        HTTP 요청 객체
     * @param response       HTTP 응답 객체
     * @param chain          필터 체인
     * @param authentication 인증 정보
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        Long userId = customUserDetails.getUserId();
        String role = auth.getAuthority();

        createAccessToken(response, userId, role);
        String refreshToken = authService.createRefreshToken(userId, role);
        authService.saveRefreshToken(userId, refreshToken);
        addCookie(response, REFRESH_TOKEN, refreshToken, COOKIE_EXPIRATION_TIME);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 인증 실패 시 호출되는 메소드 실패 상태를 응답에 설정한다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param failed   인증 실패 예외
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * 액세스 토큰을 생성하고 응답에 포함시킨다.
     *
     * @param response HTTP 응답 객체
     * @param userId   사용자 ID
     * @param role     사용자 역할
     */
    private void createAccessToken(HttpServletResponse response, Long userId, String role) {

        String accessToken = authService.createAccessToken(userId, role);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiResponse result = ApiUtil.success(new CreateAccessTokenResponse(accessToken));
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
