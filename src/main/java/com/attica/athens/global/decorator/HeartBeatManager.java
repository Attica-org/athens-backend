package com.attica.athens.global.decorator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HeartBeatManager {
    private static final long RECONNECT_THRESHOLD_MILLIS = 10000;

    private final Map<String, LocalDateTime> lastHeartBeatTimes = new ConcurrentHashMap<>();
    private final TreeMap<LocalDateTime, Set<String>> expirationTimes = new TreeMap<>();

    public void handleHeartbeat(String sessionId) {
        LocalDateTime now = LocalDateTime.now();
        updateHeartbeatTimes(sessionId, now);
        log.info("Heartbeat received from session: {}", sessionId);
    }

    private void updateHeartbeatTimes(String sessionId, LocalDateTime now) {
        LocalDateTime oldTime = lastHeartBeatTimes.put(sessionId, now);
        removeOldExpirationTime(sessionId, oldTime);
        addNewExpirationTime(sessionId, now);
    }

    private void removeOldExpirationTime(String sessionId, LocalDateTime oldTime) {
        if (oldTime != null) {
            expirationTimes.computeIfPresent(oldTime, (k, v) -> {
                v.remove(sessionId);
                return v.isEmpty() ? null : v;
            });
        }
    }

    private void addNewExpirationTime(String sessionId, LocalDateTime now) {
        expirationTimes.computeIfAbsent(now, k -> new HashSet<>()).add(sessionId);
    }

    public boolean isReconnectValid(String sessionId) {
        LocalDateTime lastHeartbeat = lastHeartBeatTimes.get(sessionId);
        log.debug("Last heartbeat for session {}: {}", sessionId, lastHeartbeat);
        log.debug("Current time: {}", LocalDateTime.now());
        if (lastHeartbeat != null) {
            Duration disconnectDuration = Duration.between(lastHeartbeat, LocalDateTime.now());
            return disconnectDuration.toMillis() <= RECONNECT_THRESHOLD_MILLIS;
        }
        return false;
    }

    public void removeSession(String sessionId) {
        LocalDateTime oldTime = lastHeartBeatTimes.remove(sessionId);
        removeOldExpirationTime(sessionId, oldTime);
        log.info("Session removed: {}", sessionId);
    }

    public Map<String, LocalDateTime> getLastHeartBeatTimes() {
        return lastHeartBeatTimes;
    }

    public TreeMap<LocalDateTime, Set<String>> getExpirationTimes() {
        return expirationTimes;
    }
}