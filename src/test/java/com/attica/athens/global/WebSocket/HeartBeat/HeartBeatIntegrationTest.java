package com.attica.athens.global.WebSocket.HeartBeat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.config.WebSocketConfig;
import com.attica.athens.global.decorator.HeartBeatManager;
import com.attica.athens.global.handler.MessageHandler.MessageProcessorFactory;
import com.attica.athens.global.interceptor.JwtChannelInterceptor;
import com.attica.athens.support.WebSocketIntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
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

    private MessageProcessorFactory messageProcessorFactory;
    private JwtChannelInterceptor interceptor;

    private final String META_TOPIC_URL = "/topic/agoras/{agoraId}";
    private static final int HEARTBEAT_INTERVAL = 10;
    private static final int MAX_WAIT_TIME = HEARTBEAT_INTERVAL * 3;

    @BeforeEach
    void setup() {
        setupStompClient();
        // 새로운 구조에서는 MessageProcessorFactory를 통해 메시지 처리를 관리합니다
        messageProcessorFactory = new MessageProcessorFactory(authService, heartBeatManager);
        interceptor = new JwtChannelInterceptor(messageProcessorFactory);
    }

    @Test
    @DisplayName("명시적으로 특정 메시지 전송할 시 하트비트를 통해 sessionId에 대한 시간이 업데이트 된다.")
    void 성공_명시적하트비트메시지전송_유효한파라미터() throws Exception {
        // Given
        // 테스트를 위한 기본 설정과 연결을 수행합니다
        Long agoraId = 1L;
        String metaTopicUrl = META_TOPIC_URL.replace("{agoraId}", agoraId.toString());
        CompletableFuture<String> resultFuture = new CompletableFuture<>();

        // WebSocket 연결 및 구독을 설정합니다
        connectAndSubscribe(metaTopicUrl, "EnvironmentalActivist", agoraId, resultFuture);
        resultFuture.get(5, TimeUnit.SECONDS);

        // 첫 번째 하트비트 시간을 확인합니다
        String sessionId = heartBeatManager.getHeartbeatTimes().values().stream()
                .flatMap(Set::stream)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No session found"));

        LocalDateTime firstHeartBeatTime = heartBeatManager.getHeartbeatTimes().entrySet().stream()
                .filter(entry -> entry.getValue().contains(sessionId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No heartbeat time found for session"));

        assertNotNull(firstHeartBeatTime, "Initial heartbeat time should not be null");

        // 하트비트 메시지를 생성합니다
        SimpMessageHeaderAccessor accessor = StompHeaderAccessor.createForHeartbeat();
        accessor.setSessionId(sessionId);
        byte[] payload = new byte[]{'\n'};
        Message<?> heartbeatMessage = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());

        // When & Then
        // 하트비트 업데이트를 검증합니다
        await().atMost(MAX_WAIT_TIME, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // 새로운 구조에서도 인터셉터를 통해 하트비트 메시지를 처리합니다
                    interceptor.preSend(heartbeatMessage, null);

                    // 현재 하트비트 시간을 확인합니다
                    LocalDateTime currentHeartBeatTime = heartBeatManager.getHeartbeatTimes().entrySet().stream()
                            .filter(entry -> entry.getValue().contains(sessionId))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("No current heartbeat time found for session"));

                    // 하트비트 시간이 정상적으로 업데이트되었는지 검증합니다
                    assertNotNull(currentHeartBeatTime, "Current heartbeat time should not be null");
                    assertNotEquals(firstHeartBeatTime, currentHeartBeatTime, "Heartbeat time should be updated");
                    assertTrue(currentHeartBeatTime.isAfter(firstHeartBeatTime),
                            "New heartbeat time should be after the initial time");
                });

        // 최종 하트비트 시간을 확인하고 검증합니다
        LocalDateTime finalHeartBeatTime = heartBeatManager.getHeartbeatTimes().entrySet().stream()
                .filter(entry -> entry.getValue().contains(sessionId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No final heartbeat time found for session"));

        assertTrue(finalHeartBeatTime.isAfter(firstHeartBeatTime),
                "Final heartbeat time should be after the initial time");
    }
}
