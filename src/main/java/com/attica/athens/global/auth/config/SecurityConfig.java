package com.attica.athens.global.auth.config;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.auth.application.CustomOAuth2UserService;
import com.attica.athens.global.auth.config.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.attica.athens.global.auth.config.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.attica.athens.global.auth.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.attica.athens.global.auth.filter.JwtAuthenticationEntryPoint;
import com.attica.athens.global.auth.filter.JwtFilter;
import com.attica.athens.global.auth.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String[] PUBLIC_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/ws/**",
            "/login/**",
            "/api/v1/auth/reissue",
            "/api/v1/temp-user/**",
            "/api/v1/open/**",
            "/oauth2/**"
    };

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthService authService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauthSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauthFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/api/v1/test/**").hasRole("TEMP_USER")
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .anyRequest().authenticated()
                );

        // LoginFilter 등록 (/login시 동작)
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),
                                authService),
                        UsernamePasswordAuthenticationFilter.class);

        // JWTFilter 등록 (모든 요청에 대해 동작)
        http
                .addFilterBefore(new JwtFilter(authService), LoginFilter.class)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        // 세션 설정 (statelss하도록)
        http
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .headers(headers -> headers
                        .frameOptions(FrameOptionsConfig::sameOrigin)
                );

        // OAuth2 로그인
        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                        .successHandler(oauthSuccessHandler)
                        .failureHandler(oauthFailureHandler)
                ).oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
