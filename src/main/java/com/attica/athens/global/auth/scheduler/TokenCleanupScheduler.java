package com.attica.athens.global.auth.scheduler;

import com.attica.athens.global.auth.dao.RefreshTokenRepository;
import com.attica.athens.global.auth.domain.RefreshToken;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
        Date date = getDate();
        List<RefreshToken> refreshTokensByExpiration = refreshTokenRepository.findByExpirationBefore(date);
        refreshTokenRepository.deleteAll(refreshTokensByExpiration);
    }

    public Date getDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusDays(1);
        Date date = Date.from(before.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }
}
