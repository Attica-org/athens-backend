package com.attica.athens.config;

import com.attica.athens.global.auth.domain.CustomUserDetails;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestSecurityConfig {

    @TestConfiguration
    public static class TestCustomUserDetailsServiceConfig {

        private static final Map<String, CustomUserDetails> customUserDetailsMap = new HashMap<>();

        static {
            initializeCustomUserDetailsMap();
        }

        private static void initializeCustomUserDetailsMap() {
            // agoraId:1 (Queued), Pros
            customUserDetailsMap.put("EnvironmentalActivist", new CustomUserDetails(1L, "password", "ROLE_USER"));
            // agoraId:1 (Queued), Observer
            customUserDetailsMap.put("PolicyExpert", new CustomUserDetails(3L, "password", "ROLE_USER"));
            // agoraId:2 (Running), Pros
            customUserDetailsMap.put("TeacherUnion", new CustomUserDetails(4L, "password", "ROLE_USER"));
            // agoraId:3 (Closed), Pros
            customUserDetailsMap.put("EconomistPro", new CustomUserDetails(7L, "password", "ROLE_USER"));
            // agoraId:5 (Queued), Pros, EnergyAnalystPros (KAKAO)
            customUserDetailsMap.put("EnergyAnalystPros", new CustomUserDetails(20L, "password", "ROLE_USER"));
            // agoraId:5 (Queued), Cons, EnergyAnalystCons (GOOGLE)
            customUserDetailsMap.put("EnergyAnalystCons", new CustomUserDetails(21L, "password", "ROLE_USER"));
        }

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
