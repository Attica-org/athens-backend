package com.attica.athens.global.config;

import com.attica.athens.global.security.CustomUserDetails;
import com.attica.athens.global.utils.WebSocketUtils;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            String userId = getWebSocketUserId();
            if (userId != null) {
                return Optional.of(userId);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new IllegalStateException("AuditorAware : principal is missing");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return Optional.of(((CustomUserDetails) principal).getUsername());
            }

            return Optional.of(principal.toString());
        };
    }

    private String getWebSocketUserId() {
        return Optional.ofNullable(WebSocketUtils.getSessionAttributes())
                .map(sessionAttributes -> (String) sessionAttributes.get("userId"))
                .orElse(null);
    }
}
