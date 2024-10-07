package com.attica.athens.domain.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.request.SendReactionRequest;
import com.attica.athens.support.WebSocketIntegrationTestSupport;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class ChatWebSocketApiIntegrationTest extends WebSocketIntegrationTestSupport {

    private final String CHAT_TOPIC_URL = "/topic/agoras/{agoraId}/chats";
    private final String CHAT_APP_URL = "/app/agoras/{agoraId}/chats";
    private final String REACTION_APP_URL = "/app/agoras/{agoraId}/chats/{chatId}/reactions";
    private final String REACTION_TOPIC_URL = "/topic/agoras/{agoraId}/reactions";
    private final String ERROR_URL = "/user/queue/errors";

    @BeforeAll
    void setup() {
        setupStompClient();

    }

    @Nested
    @DisplayName("채팅 생성시")
    class SendChatTest {

        @Test
        @DisplayName("유효한 메시지를 전송하면 성공적으로 채팅이 전송된다")
        @Sql("/sql/send-valid-chat.sql")
        void 성공_채팅생성_유효한파라미터사용() throws Exception {
            // given
            Long agoraId = 1L;
            String url = CHAT_TOPIC_URL.replace("{agoraId}", agoraId.toString());
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(url, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, "안녕하세요.");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertThat(result).contains("안녕하세요.");
        }

        @Test
        @DisplayName("10000자를 초과하는 메시지를 작성하면 에러를 반환한다")
        @Sql("/sql/send-valid-chat.sql")
        void 실패_채팅전송_메시지길이초과() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            String longMessage = "a".repeat(10001);
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, longMessage);
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1001");
                assertThat(result).contains("Content length exceeds maximum limit of 10000 characters");
            });
        }

        @Test
        @DisplayName("type을 입력하지않으면 에러를 반환한다")
        @Sql("/sql/send-valid-chat.sql")
        void 실패_채팅전송_type미입력() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(null, "안녕하세요.");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1001");
                assertThat(result).contains("Chat type cannot be null");
            });
        }

        @Test
        @DisplayName("빈 메세지를 입력하면 에러를 반환한다")
        @Sql("/sql/send-valid-chat.sql")
        void 실패_채팅전송_빈메세지() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, "");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1001");
                assertThat(result).contains("Chat message cannot be empty");
            });
        }

        @Test
        @DisplayName("존재하지 않는 아고라 id를 입력하면 에러를 반환한다")
        @Sql("/sql/send-valid-chat.sql")
        void 실패_채팅전송_존재하지않은AgoraId() throws Exception {
            // given
            Long agoraId = 9999L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, "안녕하세요.");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1301");
                assertThat(result).contains("Not found agora.");
            });
        }

        @Test
        @DisplayName("유저가 아고라에 참여하지 않은 경우 에러를 반환한다")
        @Sql("/sql/send-valid-chat.sql")
        void 실패_채팅전송_비참여유저() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "TeacherUnion", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, "안녕하세요.");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1102");
                assertThat(result).contains("User is not participating in the agora");
            });
        }

        @Test
        @DisplayName("유저가 관찰자일 경우 에러를 반환한다")
        @Sql("/sql/send-valid-chat.sql")
        void 실패_채팅전송_관찰자() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "PolicyExpert", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, "안녕하세요.");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1102");
                assertThat(result).contains("Observer cannot send this request");
            });
        }

        @Test
        @DisplayName("아고라가 이미 종료되었을 경우 에러를 반환한다")
        void 실패_채팅전송_종료된아고라() throws Exception {
            // given
            Long agoraId = 3L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EconomistPro", agoraId, resultFuture);

            // when
            SendChatRequest chatRequest = new SendChatRequest(ChatType.CHAT, "안녕하세요.");
            session.send(CHAT_APP_URL.replace("{agoraId}", agoraId.toString()), chatRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1002");
                assertThat(result).contains("Agora is closed");
            });
        }
    }

    @Nested
    @DisplayName("채팅 반응 생성시")
    class SendReactionTest {
        @DisplayName("참여자일 경우 성공적으로 반응이 전송된다")
        @Sql("/sql/send-valid-reaction.sql")
        void 성공_반응생성_참여자() throws Exception {
            // given
            Long agoraId = 1L;
            String url = REACTION_TOPIC_URL.replace("{agoraId}", agoraId.toString());
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(url, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            Long chatId = 2L;
            SendReactionRequest reactionRequest = new SendReactionRequest(ChatType.REACTION, ReactionType.LIKE);
            session.send(REACTION_APP_URL.replace("{agoraId}", agoraId.toString())
                    .replace("{chatId}", chatId.toString()), reactionRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("REACTION");
                assertThat(result).contains("\"chatId\":2");
                assertThat(result).contains(
                        "reactionCount\":{\"LIKE\":1,\"DISLIKE\":0,\"LOVE\":0,\"HAPPY\":0,\"SAD\":0}}}");
            });
        }

        @Test
        @DisplayName("존재하지 않은 agoraId를 전송하면 에러를 반환한다")
        @Sql("/sql/send-valid-reaction.sql")
        void 실패_반응전송_유효하지않은AgoraId() throws Exception {
            // given
            Long agoraId = 999L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            Long chatId = 2L;
            SendReactionRequest reactionRequest = new SendReactionRequest(ChatType.REACTION, ReactionType.LIKE);
            session.send(REACTION_APP_URL.replace("{agoraId}", agoraId.toString())
                    .replace("{chatId}", chatId.toString()), reactionRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1301");
                assertThat(result).contains("Not found agora. agoraId: 999");
            });
        }

        @Test
        @DisplayName("존재하지 않은 chatId를 전송하면 에러를 반환한다")
        @Sql("/sql/send-valid-reaction.sql")
        void 실패_반응전송_유효하지않은ChatId() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            Long chatId = 999L;
            SendReactionRequest reactionRequest = new SendReactionRequest(ChatType.REACTION, ReactionType.LIKE);
            session.send(REACTION_APP_URL.replace("{agoraId}", agoraId.toString())
                    .replace("{chatId}", chatId.toString()), reactionRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1301");
                assertThat(result).contains("Not found chat. chatId: 999");
            });
        }

        @Test
        @DisplayName("아고라에 참여하지 않은 유저일 경우 에러를 반환한다")
        @Sql("/sql/send-valid-reaction.sql")
        void 실패_반응전송_아고라참여하지않은유저() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "TeacherUnion", agoraId, resultFuture);

            // when
            Long chatId = 2L;
            SendReactionRequest reactionRequest = new SendReactionRequest(ChatType.REACTION, ReactionType.LIKE);
            session.send(REACTION_APP_URL.replace("{agoraId}", agoraId.toString())
                    .replace("{chatId}", chatId.toString()), reactionRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1102");
                assertThat(result).contains("User is not participating in the agora");
            });
        }

        @Test
        @DisplayName("자신의 댓글일 경우 에러를 반환한다")
        @Sql("/sql/send-valid-reaction.sql")
        void 실패_반응전송_자신의댓글() throws Exception {
            // given
            Long agoraId = 1L;
            CompletableFuture<String> resultFuture = new CompletableFuture<>();
            StompSession session = connectAndSubscribe(ERROR_URL, "EnvironmentalActivist", agoraId, resultFuture);

            // when
            Long chatId = 1L;
            SendReactionRequest reactionRequest = new SendReactionRequest(ChatType.REACTION, ReactionType.LIKE);
            session.send(REACTION_APP_URL.replace("{agoraId}", agoraId.toString())
                    .replace("{chatId}", chatId.toString()), reactionRequest);

            // then
            String result = resultFuture.get(10, TimeUnit.SECONDS);
            assertAll(() -> {
                assertThat(result).contains("ERROR");
                assertThat(result).contains("1102");
                assertThat(result).contains("Chat writers cannot respond to themselves.");
            });
        }
    }
}
