package com.attica.athens.domain.chat.domain;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.chat.component.BadWordFilter;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.support.IntegrationTestSupport;
import com.attica.athens.support.annotation.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("채팅 인증 API 통합 테스트")
public class ChatAuthApiIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private BadWordFilter badWordFilter;

    @Nested
    @DisplayName("채팅 비속어 필터 테스트")
    class checkBadWord {

        @Test
        @DisplayName("채팅 내의 비속어 검사")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser
        void 성공_비속어검사_비속어포함된채팅() throws Exception {
            //given
            badWordFilter.init();
            SendChatRequest request = new SendChatRequest(ChatType.CHAT, "토론 병신같이 하네");

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            //when
            final ResultActions result = mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/chats/filter", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            result.andExpectAll(
                    jsonPath("$.response.hasBadWord").value(true),
                    jsonPath("$.response.badword").isNotEmpty(),
                    jsonPath("$.response.badword[0].start").value(3),
                    jsonPath("$.response.badword[0].end").value(4),
                    jsonPath("$.response.badword[0].keyword").value("병신")
            );
        }

        @Test
        @DisplayName("채팅 내에 비속어가 없는 경우 200OK응답")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser
        void 성공_비속어검사_비속어포함되지않은채팅() throws Exception {
            //given
            badWordFilter.init();
            SendChatRequest request = new SendChatRequest(ChatType.CHAT, "토론 잘하시네요");

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            //when
            final ResultActions result = mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/chats/filter", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.response.hasBadWord").value(false),
                            jsonPath("$.response.badword").isEmpty()
                    );
        }

        @Test
        @DisplayName("유효하지 않은 아고라멤버로 채팅 검사를 한다")
        @WithMockCustomUser("TeacherUnion")
        void 실패_비속어필터_유효하지않은AgoraMember() throws Exception {
            // given
            SendChatRequest request = new SendChatRequest(ChatType.CHAT, "토론 병신같이 하네");

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/chats/filter", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1102),
                            jsonPath("$.error.message").value("User is not participating in the agora"),
                            jsonPath("$.response").doesNotExist()
                    );
        }
    }
}
