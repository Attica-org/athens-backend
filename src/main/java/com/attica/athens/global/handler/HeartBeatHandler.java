package com.attica.athens.global.handler;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agoraMember.application.AgoraMemberService;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.global.decorator.HeartBeatManager;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartBeatHandler {
    private static final int INACTIVITY_THRESHOLD_MINUTES = 2;

    private final HeartBeatManager heartBeatManager;
    private final AgoraService agoraService;
    private final AgoraMemberService agoraMemberService;

    @Scheduled(fixedRate = 60000)
    public void checkInactiveSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime offTime = now.minusMinutes(INACTIVITY_THRESHOLD_MINUTES);
        log.debug("Checking inactive sessions. Current time: {}, Off time: {}", now, offTime);

        TreeMap<LocalDateTime, Set<String>> heartbeatTimes = heartBeatManager.getHeartbeatTimes();

        processExpiredSessions(heartbeatTimes, offTime);

        logRemainingActiveSessions(heartbeatTimes);
    }

    private void processExpiredSessions(TreeMap<LocalDateTime, Set<String>> heartbeatTimes, LocalDateTime offTime) {
        while (!heartbeatTimes.isEmpty() && heartbeatTimes.firstKey().isBefore(offTime)) {
            Map.Entry<LocalDateTime, Set<String>> entry = heartbeatTimes.pollFirstEntry();
            log.debug("Processing entry with time: {}", entry.getKey());
            for (String sessionId : entry.getValue()) {
                handleInactiveSession(sessionId);
            }
        }
    }

    private void handleInactiveSession(String sessionId) {
        log.info("Inactive session detected: {}", sessionId);
        try {
            AgoraMember agoraMember = agoraMemberService.findAgoraMember(sessionId);

            agoraService.exit(agoraMember.getMember().getId());
            processDisconnection(sessionId, agoraMember.getAgora().getId(), agoraMember.getMember().getId());
            log.info("Member exited due to inactivity: agoraId = {}, memberId={}", agoraMember.getAgora().getId(),
                    agoraMember.getMember().getId());
        } catch (Exception e) {
            log.error("Error handling inactive session: {}", sessionId, e);
        }
    }

    @Transactional
    public void processDisconnection(String sessionId, Long agoraId, Long memberId) {
        agoraMemberService.removeSessionId(sessionId);
        agoraMemberService.sendMetaToActiveMembers(agoraId, memberId);
        log.info("WebSocket Disconnected: agoraId={}, userId={}", agoraId, memberId);
    }

    private void logRemainingActiveSessions(TreeMap<LocalDateTime, Set<String>> heartbeatTimes) {
        heartbeatTimes.values().stream()
                .flatMap(Set::stream)
                .forEach(sessionId -> log.debug("Active session after check: {}", sessionId));
    }
}
