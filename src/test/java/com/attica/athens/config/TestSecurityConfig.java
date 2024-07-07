package com.attica.athens.config;

import com.attica.athens.global.auth.CustomUserDetails;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestSecurityConfig {

    @TestConfiguration
    public static class SecurityConfig {

        private final Map<String, CustomUserDetails> customUserDetailsMap = new HashMap<>() {
            {
                put("EnvironmentalActivist", new CustomUserDetails(1L, "password", "ROLE_USER"));
                put("EconomistPro", new CustomUserDetails(7L, "password", "ROLE_USER"));
            }
        };

        @Bean
        public UserDetailsService testCustomUserDetailsService() {
            return username -> {
                if (customUserDetailsMap.containsKey(username)) {
                    return customUserDetailsMap.get(username);
                }
                throw new UsernameNotFoundException("Test User Not Found");
            };
        }
    }
}
