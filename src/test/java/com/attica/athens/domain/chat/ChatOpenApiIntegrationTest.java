package com.attica.athens.domain.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("채팅 오픈 API 통합 테스트")
public class ChatOpenApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("채팅방 내역 조회 테스트")
    class GetChatHistoryTest {

        @Test
        @DisplayName("채팅방 내역을 조회한다")
        void getChatHistory() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    get("/api/v1/open/agoras/1/chats", API_V1, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.error").doesNotExist(),
                            jsonPath("$.response.chats").isArray(),
                            jsonPath("$.response.chats.length()").value(3),
                            jsonPath("$.response.chats[0].chatId").exists(),
                            jsonPath("$.response.chats[0].user.nickname").isString(),
                            jsonPath("$.response.chats[0].content").isString(),
                            jsonPath("$.response.meta.effectiveSize").value(10)
                    );
        }
    }
}
