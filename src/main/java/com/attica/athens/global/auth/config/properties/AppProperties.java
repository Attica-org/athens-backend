package com.attica.athens.global.auth.config.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
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
        private TempToken tempToken = new TempToken();


        @Getter
        @Setter
        public static class Jwt {
            private String secretKey;
            private long accessExpired;
            private long refreshExpired;
        }

        @Getter
        @Setter
        public static class TempToken {
            private int expirationMinutes;
        }
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private List<String> redirectUris = new ArrayList<>();
        private String defaultRedirectUri;

        public void setRedirectUris(String redirectUrisString) {
            this.redirectUris = Arrays.asList(redirectUrisString.split(","));
        }
    }
}
