package com.attica.athens.global.decorator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartBeatManager {
    private static final long RECONNECT_THRESHOLD_MILLIS = 10000;

    private final TreeMap<LocalDateTime, Set<String>> heartbeatTimes = new TreeMap<>();

    public void handleHeartbeat(String sessionId) {
        LocalDateTime now = LocalDateTime.now();
        updateHeartbeatTimes(sessionId, now);
        log.info("Heartbeat received from session: {}", sessionId);
    }

    private void updateHeartbeatTimes(String sessionId, LocalDateTime now) {
        heartbeatTimes.values().forEach(sessions -> sessions.remove(sessionId));
        heartbeatTimes.computeIfAbsent(now, k -> new HashSet<>()).add(sessionId);
    }

    public boolean isReconnectValid(String sessionId) {
        Optional<Entry<LocalDateTime, Set<String>>> entry = heartbeatTimes.entrySet().stream()
                .filter(e -> e.getValue().contains(sessionId))
                .findFirst();

        if (entry.isPresent()) {
            Duration disconnectDuration = Duration.between(entry.get().getKey(), LocalDateTime.now());
            return disconnectDuration.toMillis() <= RECONNECT_THRESHOLD_MILLIS;
        }
        return false;
    }

    public void removeSession(String sessionId) {
        heartbeatTimes.values().forEach(sessions -> sessions.remove(sessionId));
        log.info("Session removed: {}", sessionId);
    }

    public TreeMap<LocalDateTime, Set<String>> getHeartbeatTimes() {
        return new TreeMap<>(heartbeatTimes);
    }
}
