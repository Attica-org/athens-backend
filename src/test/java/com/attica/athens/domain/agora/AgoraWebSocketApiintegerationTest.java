package com.attica.athens.domain.agora;

import static org.assertj.core.api.Assertions.assertThat;

import com.attica.athens.support.WebSocketIntegrationTestSupport;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class AgoraWebSocketApiintegerationTest extends WebSocketIntegrationTestSupport {

    private final String CHAT_TOPIC_URL = "/topic/agoras/{agoraId}/chats";
    private final String META_TOPIC_URL = "/topic/agoras/{agoraId}";

    @BeforeAll
    void setup() {
        setupStompClient();
    }

    @Test
    @Sql("/sql/send-valid-chat.sql")
    @DisplayName("아고라에 다른 사용자가 입장하면 아고라에 참여하는 유저는 업데이트된 메타 정보를 받는다")
    void 성공_아고라입장_유효한AgoraId() throws Exception {
        // given
        Long agoraId = 1L;
        String metaTopicUrl = META_TOPIC_URL.replace("{agoraId}", agoraId.toString());
        CompletableFuture<String> resultFuture = new CompletableFuture<>();
        connectAndSubscribe(metaTopicUrl, "EnvironmentalActivist", agoraId, resultFuture);

        // when
        String url = CHAT_TOPIC_URL.replace("{agoraId}", agoraId.toString());
        CompletableFuture<String> chatResultFuture = new CompletableFuture<>();
        connectAndSubscribe(url, "PolicyExpert", agoraId, chatResultFuture);

        // then
        String result = resultFuture.get(15, TimeUnit.SECONDS);
        assertThat(result).contains(
                "participants\":[{\"type\":\"OBSERVER\",\"count\":1},{\"type\":\"PROS\",\"count\":1}]");
    }

    @Test
    @Sql("/sql/send-valid-chat.sql")
    @DisplayName("아고라에 다른 사용자가 퇴장하면 아고라에 참여하는 유저는 업데이트된 메타 정보를 받는다")
    void 성공_아고라퇴장_유효한AgoraId() throws Exception {
        // given
        Long agoraId = 1L;
        String url = CHAT_TOPIC_URL.replace("{agoraId}", agoraId.toString());
        CompletableFuture<String> chatResultFuture = new CompletableFuture<>();
        final StompSession session = connectAndSubscribe(url, "PolicyExpert", agoraId, chatResultFuture);

        String metaTopicUrl = META_TOPIC_URL.replace("{agoraId}", agoraId.toString());
        CompletableFuture<String> resultFuture = new CompletableFuture<>();
        connectAndSubscribe(metaTopicUrl, "EnvironmentalActivist", agoraId, resultFuture);

        // when
        session.disconnect();

        // then
        String result = resultFuture.get(15, TimeUnit.SECONDS);
        assertThat(result).contains("participants\":[{\"type\":\"PROS\",\"count\":1}]");
    }
}
