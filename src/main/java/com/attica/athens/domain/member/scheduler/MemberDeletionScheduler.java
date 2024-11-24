package com.attica.athens.domain.member.scheduler;

import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDeletionScheduler {

    private final MemberRepository memberRepository;
    private static final int RETENTION_PERIOD_MONTHS = 6;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void deleteExpiredMembers() {
        try {
            LocalDateTime expirationDate = LocalDateTime.now().minusMonths(RETENTION_PERIOD_MONTHS);
            List<Member> expiredMembers = memberRepository
                    .findByIsDeletedTrueAndDeletedAtBefore(expirationDate);

            if (expiredMembers.isEmpty()) {
                log.info("No expired members to delete");
                return;
            }

            log.info("Starting permanent deletion of {} members", expiredMembers.size());
            memberRepository.deleteAll(expiredMembers);
            log.info("Completed permanent deletion of {} members", expiredMembers.size());

        } catch (Exception e) {
            log.error("Error during permanent member deletion: {}", e.getMessage(), e);
        }
    }
}
