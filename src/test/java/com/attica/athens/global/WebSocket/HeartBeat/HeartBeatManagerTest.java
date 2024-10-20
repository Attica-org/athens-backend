package com.attica.athens.global.WebSocket.HeartBeat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attica.athens.global.decorator.HeartBeatManager;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HeartBeat 테스트")
public class HeartBeatManagerTest {

    @Test
    @DisplayName("HandleHeartBeat 테스트")
    void 성공_handleHeartBeat_유효한파라미터() {
        // given
        HeartBeatManager manager = new HeartBeatManager();
        String sessionId = "test-session";

        // when
        manager.handleHeartbeat(sessionId);

        // then
        TreeMap<LocalDateTime, Set<String>> heartbeatTimes = manager.getHeartbeatTimes();
        assertTrue(heartbeatTimes.values().stream().anyMatch(set -> set.contains(sessionId)));
        assertEquals(1, heartbeatTimes.size());
    }

    @Nested
    @DisplayName("재연결 검사")
    class reconnectTest {

        @Test
        @DisplayName("재연결 유효성 검사 성공")
        void 성공_유효성검사_유효한파라미터() {
            // given
            HeartBeatManager manager = new HeartBeatManager();
            String sessionId = "test-session";

            // when
            manager.handleHeartbeat(sessionId);

            //then
            assertTrue(manager.isReconnectValid(sessionId));
        }

        @Test
        @DisplayName("재연결 유효성 검사 실패")
        void 실패_유효성검사_유효하지않은파라미터() throws Exception {
            // given
            HeartBeatManager manager = new HeartBeatManager();
            String sessionId = "test-session";

            // when
            Field heartbeatTimesField = HeartBeatManager.class.getDeclaredField("heartbeatTimes");
            heartbeatTimesField.setAccessible(true);
            TreeMap<LocalDateTime, Set<String>> heartbeatTimes = (TreeMap<LocalDateTime, Set<String>>) heartbeatTimesField.get(
                    manager);

            LocalDateTime oldTime = LocalDateTime.now().minusSeconds(11);
            heartbeatTimes.put(oldTime, Set.of(sessionId));

            // then
            assertFalse(manager.isReconnectValid(sessionId));
        }
    }

    @Test
    @DisplayName("세션지우기 테스트")
    void 성공_세션지우기_유효한파라미터() {
        HeartBeatManager manager = new HeartBeatManager();
        String sessionId = "test-session";

        manager.handleHeartbeat(sessionId);
        manager.removeSession(sessionId);

        TreeMap<LocalDateTime, Set<String>> heartbeatTimes = manager.getHeartbeatTimes();
        assertTrue(heartbeatTimes.values().stream().noneMatch(set -> set.contains(sessionId)));
    }
}
