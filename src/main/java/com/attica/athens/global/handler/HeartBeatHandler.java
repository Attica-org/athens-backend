package com.attica.athens.global.handler;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agoraMember.application.AgoraMemberService;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.exception.NotFoundAgoraMemberException;
import com.attica.athens.domain.member.dao.MemberRepository;
import com.attica.athens.domain.member.domain.BaseMember;
import com.attica.athens.domain.member.exception.NotFoundMemberException;
import com.attica.athens.global.decorator.HeartBeatManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartBeatHandler {
    private static final int INACTIVITY_THRESHOLD_MINUTES = 2;

    private final HeartBeatManager heartBeatManager;
    private final AgoraService agoraService;
    private final MemberRepository memberRepository;
    private final AgoraMemberService agoraMemberService;
    private final AgoraMemberRepository agoraMemberRepository;

    @Scheduled(fixedRate = 60000)
    public void checkInactiveSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime offTime = now.minusMinutes(INACTIVITY_THRESHOLD_MINUTES);
        log.debug("Checking inactive sessions. Current time: {}, Off time: {}", now, offTime);

        TreeMap<LocalDateTime, Set<String>> expirationTimes = heartBeatManager.getExpirationTimes();
        Map<String, LocalDateTime> lastHeartBeatTimes = heartBeatManager.getLastHeartBeatTimes();

        processExpiredSessions(expirationTimes, lastHeartBeatTimes, offTime);

        logRemainingActiveSessions(lastHeartBeatTimes);
    }

    private void processExpiredSessions(TreeMap<LocalDateTime, Set<String>> expirationTimes,
                                        Map<String, LocalDateTime> lastHeartBeatTimes,
                                        LocalDateTime offTime) {
        while (!expirationTimes.isEmpty() && expirationTimes.firstKey().isBefore(offTime)) {
            Map.Entry<LocalDateTime, Set<String>> entry = expirationTimes.pollFirstEntry();
            log.debug("Processing entry with time: {}", entry.getKey());
            for (String sessionId : entry.getValue()) {
                processSession(sessionId, lastHeartBeatTimes, offTime);
            }
        }
    }

    private void processSession(String sessionId, Map<String, LocalDateTime> lastHeartBeatTimes,
                                LocalDateTime offTime) {
        log.debug("Checking session: {}. Last heartbeat: {}", sessionId, lastHeartBeatTimes.get(sessionId));
        if (lastHeartBeatTimes.get(sessionId).isBefore(offTime)) {
            handleInactiveSession(sessionId);
            heartBeatManager.removeSession(sessionId);
        } else {
            log.debug("Session {} is still active", sessionId);
        }
    }

    private void handleInactiveSession(String sessionId) {
        log.info("Inactive session detected: {}", sessionId);
        try {
            AgoraMember agoraMember = findAgoraMember(sessionId);
            BaseMember baseMember = findBaseMember(agoraMember.getId());

            agoraService.exit(baseMember.getId());
            processDisconnection(sessionId, agoraMember.getAgora().getId(), baseMember.getId());
            log.info("Member exited due to inactivity: agoraId = {}, memberId={}", agoraMember.getAgora().getId(),
                    baseMember.getId());
        } catch (Exception e) {
            log.error("Error handling inactive session: {}", sessionId, e);
        }
    }

    private AgoraMember findAgoraMember(String sessionId) {
        return agoraMemberRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new NotFoundAgoraMemberException(sessionId));
    }

    private BaseMember findBaseMember(Long agoraMemberId) {
        return memberRepository.findMemberByAgoraMemberId(agoraMemberId)
                .orElseThrow(NotFoundMemberException::new);
    }

    @Transactional
    public void processDisconnection(String sessionId, Long agoraId, Long memberId) {
        agoraMemberService.removeSessionId(sessionId);
        agoraMemberService.sendMetaToActiveMembers(agoraId, memberId);
        log.info("WebSocket Disconnected: agoraId={}, userId={}", agoraId, memberId);
        agoraMemberService.deleteAgoraMember(agoraId, memberId);
    }

    private void logRemainingActiveSessions(Map<String, LocalDateTime> lastHeartBeatTimes) {
        lastHeartBeatTimes.keySet().forEach(sessionId ->
                log.debug("Active session after check: {}", sessionId)
        );
    }
}