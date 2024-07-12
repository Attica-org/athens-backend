package com.attica.athens.domain.agora;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.support.IntegrationTestSupport;
import com.attica.athens.support.annotation.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("아고라 인증 API 통합 테스트")
public class AgoraAuthApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("아고라 시작 테스트")
    class StartAgoraTest {

        @Test
        @DisplayName("아고라를 시작한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser
        void startAgora() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/start", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.error").doesNotExist(),
                            jsonPath("$.response.agoraId").value(1),
                            jsonPath("$.response.startTime").exists(),
                            jsonPath("$.response.startTime").isString(),
                            jsonPath("$.response.startTime").value(
                                    matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))
                    );
        }

        @Test
        @DisplayName("존재하지 않은 agoraId일 경우 에러를 반환한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser
        void givenNonExistentAgoraId_whenStartAgora_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/start", API_V1_AUTH, 999)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found agora. agoraId: 999")
                    );
        }

        @Test
        @DisplayName("사용자가 해당 아고라의 참여자가 아닌 경우 에러를 반환한다")
        @WithMockCustomUser("TeacherUnion")
        void givenNonParticipant_whenStartAgora_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/start", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1102),
                            jsonPath("$.error.message").value("User is not participating in the agora")
                    );
        }

        @Test
        @DisplayName("아고라가 대기상태가 아닌 경우 에러를 반환한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser("TeacherUnion")
        void givenNotQueuedAgora_whenStartAgora_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/start", API_V1_AUTH, 2)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1002),
                            jsonPath("$.error.message").value("Agora status must be QUEUED")
                    );
        }

        @Test
        @DisplayName("관찰자인 경우 에러를 반환한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser("PolicyExpert")
        void givenObserver_whenStartAgora_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/start", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isForbidden())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1102),
                            jsonPath("$.error.message").value("observer cannot send this request")
                    );
        }
    }

    @Nested
    @DisplayName("아고라 종료 투표 테스트")
    class EndAgoraVoteTest {

        @Test
        @DisplayName("아고라 종료 투표를 한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser("TeacherUnion")
        void endAgoraVote() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 2)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.agoraId").value(2),
                            jsonPath("$.response.endVoteCount").value(1),
                            jsonPath("$.response.isClosed").value(true),
                            jsonPath("$.response.endTime").exists(),
                            jsonPath("$.response.endTime").isString(),
                            jsonPath("$.response.endTime").value(
                                    matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")),
                            jsonPath("$.error").doesNotExist()
                    );
        }

        @Test
        @DisplayName("존재하지 않은 agoraId일 경우 에러를 반환한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser("TeacherUnion")
        void givenNonExistentAgoraId_whenEndAgoraVote_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 999)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // Then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found agora. agoraId: 999")
                    );
        }

        @Test
        @DisplayName("관찰자인 경우 에러를 반환한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser("PolicyExpert")
        void givenObserver_whenEndAgoraVote_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isForbidden())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1102),
                            jsonPath("$.error.message").value("observer cannot send this request")
                    );
        }

        @Test
        @DisplayName("사용자가 해당 아고라의 참여자가 아닌 경우 에러를 반환한다")
        @WithMockCustomUser("TeacherUnion")
        void givenNonParticipant_whenEndAgoraVote_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1102),
                            jsonPath("$.error.message").value("User is not participating in the agora")
                    );
        }

        @Test
        @DisplayName("아고라가 대기상태가 아닌 경우 에러를 반환한다")
        @Sql("/sql/enter-agora-members.sql")
        @WithMockCustomUser
        void givenNotQueuedAgora_whenEndAgoraVote_thenReturnsError() throws Exception {
            // Given

            // When
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            // Then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1002),
                            jsonPath("$.error.message").value("Agora status must be RUNNING")
                    );
        }
    }

    @Test
    @DisplayName("사용자가 이미 종료 투표를 한 경우 에러를 반환한다")
    @Sql("/sql/enter-agora-members.sql")
    @WithMockCustomUser("TeacherUnion")
    void givenAlreadyEndVote_whenEndAgoraVote_thenReturnsError() throws Exception {
        // Given
        mockMvc.perform(
                patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 2)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // When
        final ResultActions result = mockMvc.perform(
                patch("/{prefix}/agoras/{agoraId}/close", API_V1_AUTH, 2)
                        .contentType(MediaType.APPLICATION_JSON)

        );

        // Then
        result.andExpect(status().isConflict())
                .andExpectAll(
                        jsonPath("$.success").value(false),
                        jsonPath("$.response").doesNotExist(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error.code").value(1004),
                        jsonPath("$.error.message").value("User has already voted for ending the agora")
                );
    }
}
