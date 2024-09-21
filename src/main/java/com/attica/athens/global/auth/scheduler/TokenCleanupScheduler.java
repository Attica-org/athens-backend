package com.attica.athens.global.auth.scheduler;

import com.attica.athens.global.auth.dao.RefreshTokenRepository;
import com.attica.athens.global.auth.domain.RefreshToken;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredTokens() {
        LocalDateTime expirationDateTime = getExpirationDateTime();
        List<RefreshToken> refreshTokensByExpiration = refreshTokenRepository.findByExpirationBefore(
                expirationDateTime);
        refreshTokenRepository.deleteAll(refreshTokensByExpiration);
    }

    private LocalDateTime getExpirationDateTime() {
        return LocalDateTime.now().minusDays(1);
    }
}
