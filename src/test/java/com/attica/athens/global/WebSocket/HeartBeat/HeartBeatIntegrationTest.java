package com.attica.athens.global.WebSocket.HeartBeat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.config.WebSocketConfig;
import com.attica.athens.global.decorator.HeartBeatManager;
import com.attica.athens.global.interceptor.JwtChannelInterceptor;
import com.attica.athens.support.WebSocketIntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

@DisplayName("하트비트 통합 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebSocketConfig.class)
public class HeartBeatIntegrationTest extends WebSocketIntegrationTestSupport {

    @Autowired
    private HeartBeatManager heartBeatManager;

    @Autowired
    private AuthService authService;

    private JwtChannelInterceptor interceptor;

    private final String META_TOPIC_URL = "/topic/agoras/{agoraId}";
    private static final int HEARTBEAT_INTERVAL = 10;
    private static final int MAX_WAIT_TIME = HEARTBEAT_INTERVAL * 3;

    @BeforeEach
    void setup() {
        setupStompClient();
        interceptor = new JwtChannelInterceptor(authService, heartBeatManager);
    }

    @Test
    @DisplayName("명시적으로 특정 메시지 전송할 시 하트비트를 통해 sessionId에 대한 시간이 업데이트 된다.")
    void 성공_명시적하트비트메시지전송_유효한파라미터() throws Exception {
        // Given
        Long agoraId = 1L;
        String metaTopicUrl = META_TOPIC_URL.replace("{agoraId}", agoraId.toString());
        CompletableFuture<String> resultFuture = new CompletableFuture<>();

        // WebSocket 연결 및 구독
        connectAndSubscribe(metaTopicUrl, "EnvironmentalActivist", agoraId, resultFuture);
        resultFuture.get(5, TimeUnit.SECONDS);

        String sessionId = heartBeatManager.getLastHeartBeatTimes().keySet().iterator().next();
        LocalDateTime firstHeartBeatTime = heartBeatManager.getLastHeartBeatTimes().get(sessionId);

        assertNotNull(firstHeartBeatTime, "Initial heartbeat time should not be null");

        // 하트비트 메시지 준비
        SimpMessageHeaderAccessor accessor = StompHeaderAccessor.createForHeartbeat();
        accessor.setSessionId(sessionId);
        byte[] payload = new byte[]{'\n'};
        Message<?> heartbeatMessage = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());

        // When & Then
        await().atMost(MAX_WAIT_TIME, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // 명시적 하트비트 메시지 전송
                    interceptor.preSend(heartbeatMessage, null);

                    LocalDateTime currentHeartBeatTime = heartBeatManager.getLastHeartBeatTimes().get(sessionId);
                    assertNotNull(currentHeartBeatTime, "Current heartbeat time should not be null");
                    assertNotEquals(firstHeartBeatTime, currentHeartBeatTime, "Heartbeat time should be updated");
                    assertTrue(currentHeartBeatTime.isAfter(firstHeartBeatTime),
                            "New heartbeat time should be after the initial time");
                });

        // 최종 확인
        LocalDateTime finalHeartBeatTime = heartBeatManager.getLastHeartBeatTimes().get(sessionId);

        assertTrue(finalHeartBeatTime.isAfter(firstHeartBeatTime),
                "Final heartbeat time should be after the initial time");
    }
}