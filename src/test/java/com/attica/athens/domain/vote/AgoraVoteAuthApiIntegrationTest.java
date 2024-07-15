package com.attica.athens.domain.vote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agoraMember.domain.AgoraVoteType;
import com.attica.athens.support.IntegrationTestSupport;
import com.attica.athens.support.annotation.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("아고라 투표 API 통합 테스트")
public class AgoraVoteAuthApiIntegrationTest extends IntegrationTestSupport {

    @Test
    @DisplayName("투표 테스트")
    @Sql("/sql/enter-agora-members.sql")
    @Sql("/sql/agora-status-closed.sql")
    @WithMockCustomUser
    void vote() throws Exception {
        //given
        AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

        objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(
                patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
        );

        //then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.success").value(true),
                        jsonPath("$.response").exists(),
                        jsonPath("$.response.id").value(1),
                        jsonPath("$.response.voteType").value("PROS"),
                        jsonPath("$.error").doesNotExist()
                );
    }

    @Test
    @DisplayName("투표 결과 테스트")
    @Sql("/sql/enter-agora-members.sql")
    @Sql("/sql/agora-status-closed.sql")
    @Sql("/sql/agora-vote-update.sql")
    @WithMockCustomUser
    void voteResult() throws Exception {
        //when
        final ResultActions result = mockMvc.perform(
                get("/{prefix}/agoras/{agoraId}/results", API_V1_AUTH, 1)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.success").value(true),
                        jsonPath("$.response").exists(),
                        jsonPath("$.response.id").value(1),
                        jsonPath("$.response.prosCount").value(2),
                        jsonPath("$.response.consCount").value(1),
                        jsonPath("$.error").doesNotExist()
                );
    }

}
