package com.attica.athens.global.auth.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Auth {
        private Jwt jwt = new Jwt();

        @Getter
        @Setter
        public static class Jwt {
            private String secretKey;
            private long accessExpired;
            private long refreshExpired;
        }
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private String redirectUri;
    }
}
