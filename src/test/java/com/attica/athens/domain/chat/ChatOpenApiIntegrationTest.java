package com.attica.athens.domain.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("채팅 오픈 API 통합 테스트")
public class ChatOpenApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("채팅방 내역 조회 테스트")
    class GetChatHistoryTest {

        @Test
        @DisplayName("채팅 내역을 조회한다")
        void getChatHistory() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    get("/{apiVersion}/open/agoras/{agoraId}/chats", API_V1, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.error").doesNotExist(),
                            jsonPath("$.response.chats").isArray(),
                            jsonPath("$.response.chats.length()").value(4),
                            jsonPath("$.response.chats[0].chatId").exists(),
                            jsonPath("$.response.chats[0].user.nickname").isString(),
                            jsonPath("$.response.chats[0].content").isString(),
                            jsonPath("$.response.meta.effectiveSize").value(10)
                    );
        }
    }

    @Nested
    @DisplayName("채팅방 참여자 조회 테스트")
    class GetChatParticipantsTest {

        @Test
        @DisplayName("채팅방 참여자를 조회한다")
        @Sql("/sql/update-agora-participants.sql")
        void getChatParticipants() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    get("/{apiVersion}/open/agoras/{agoraId}/users", API_V1, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.error").doesNotExist(),
                            jsonPath("$.response.agoraId").value(1),
                            jsonPath("$.response.participants").isArray(),
                            jsonPath("$.response.participants.length()").value(5),
                            jsonPath("$.response.participants[0].id").exists(),
                            jsonPath("$.response.participants[0].nickname").isString(),
                            jsonPath("$.response.participants[0].photoNumber").isNumber(),
                            jsonPath("$.response.participants[0].type").isString()
                    );
        }
    }
}
