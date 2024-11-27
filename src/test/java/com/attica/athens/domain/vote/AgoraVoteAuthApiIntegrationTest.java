package com.attica.athens.domain.vote;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.request.KickVoteRequest;
import com.attica.athens.domain.agoraMember.domain.AgoraVoteType;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.support.IntegrationTestSupport;
import com.attica.athens.support.annotation.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("아고라 투표 API 통합 테스트")
public class AgoraVoteAuthApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("투표 테스트")
    class AgoraVoteTest {
        @Test
        @DisplayName("찬반 투표를 실행한다.")
        @Sql(scripts = {
                "/sql/enter-agora-members.sql",
                "/sql/agora-status-closed.sql"
        })
        @WithMockCustomUser
        void 성공_찬반투표_찬반타입과투표여부DTO전달() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.id").value(1),
                            jsonPath("$.response.voteType").value("PROS"),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("투표하는 유저의 ID가 존재하지 않을 경우 예외를 발생시킨다.")
        @Sql(scripts = {
                "/sql/enter-agora-members.sql",
                "/sql/agora-status-closed.sql"
        })
        @WithMockCustomUser("TeacherUnion")
        void 실패_아고라멤버조회_아고라에존재하지않는멤버ID전달() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Agora user not found with agora id: 1 user id: 4")
                    );
        }

        @Test
        @DisplayName("아고라가 존재하지 않을 경우 에외를 발생시킨다.")
        @WithMockCustomUser
        void 실패_아고라조회_존재하지않는아고라ID전달() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 50)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found agora. agoraId: 50")
                    );
        }

        @Test
        @DisplayName("투표할때 아고라의 상태가 RUNNUING이거나 QUEUED일 경우 예외를 발생시킨다.")
        @WithMockCustomUser
        void 실패_아고라상태_잘못된아고라상태전달() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1002),
                            jsonPath("$.error.message").value("Agora status must be CLOSED")
                    );
        }

        @Test
        @DisplayName("중복으로 찬반 투표 요청이 들어올 경우 예외를 발생시킨다.")
        @Sql(scripts = {
                "/sql/enter-agora-members.sql",
                "/sql/agora-status-closed.sql",
                "/sql/agoraMember-isOpinionVoted-true.sql"
        })
        @WithMockCustomUser
        void 실패_아고라멤버_이미투표여부true전달() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1002),
                            jsonPath("$.error.message").value("User has already voted for Opinion in this agora")
                    );
        }

        @Test
        @DisplayName("투표를 하지 않았을 때 예외를 발생시킨다.")
        @Sql(scripts = {
                "/sql/enter-agora-members.sql",
                "/sql/agora-status-closed.sql"
        })
        @WithMockCustomUser
        void 실패_아고라멤버_투표타입null전달() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(null, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1002),
                            jsonPath("$.error.message").value("Agora VoteType must be PROS or CONS")
                    );
        }

        @Test
        @DisplayName("Agora 종료시간과 투표 api 요청 시간 차이가 20초가 넘어간다면 예외를 발생시킨다.")
        @Sql(scripts = {
                "/sql/enter-agora-members.sql",
                "/sql/agora-endTime-update.sql"
        })
        @WithMockCustomUser
        void 실패_아고라멤버_초과된투표요청시간() throws Exception {
            // given
            AgoraVoteRequest request = new AgoraVoteRequest(AgoraVoteType.PROS, true);

            objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request);

            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1002),
                            jsonPath("$.error.message").value("Voting period has expired.")
                    );
        }
    }

    @Nested
    @DisplayName("투표 결과 테스트")
    class AgoraVoteResultTest {

        @Test
        @DisplayName("투표 결과 테스트")
        @Sql(scripts = {
                "/sql/enter-agora-members.sql",
                "/sql/agora-status-closed.sql",
                "/sql/agora-vote-update.sql"
        })
        @WithMockCustomUser
        void 성공_투표결과_유효한파라미터전달() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/agoras/{agoraId}/results", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.id").value(1),
                            jsonPath("$.response.prosCount").value(2),
                            jsonPath("$.response.consCount").value(1),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("아고라가 존재하지 않을 경우 에외를 발생시킨다.")
        @WithMockCustomUser
        void 실패_아고라조회_존재하지않는아고라ID전달() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/agoras/{agoraId}/results", API_V1_AUTH, 50)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found agora. agoraId: 50")
                    );
        }
    }

    @Nested
    @DisplayName("사용자 퇴장 투표 테스트")
    @Sql(scripts = {
            "/sql/get-category.sql",
            "/sql/get-agora.sql",
            "/sql/get-base-member.sql"
    })
    class KickVoteTest {

        @Test
        @DisplayName("퇴장 투표 한다.")
        @WithMockCustomUser
        void 성공_퇴장_투표() throws Exception {
            // given
            KickVoteRequest request = new KickVoteRequest(2L, 3);

            // when
            final ResultActions result = mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/kick-vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response").value("투표가 성공적으로 처리되었습니다."),
                            jsonPath("$.error").doesNotExist()
                    );
        }

        @Test
        @DisplayName("동일한 대상 중복 퇴장 투표 시 예외가 발생한다.")
        @WithMockCustomUser
        void 성공_중복_퇴장_투표_예외발생() throws Exception {
            // given
            KickVoteRequest request = new KickVoteRequest(2L, 3);

            // when
            mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/kick-vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            final ResultActions result = mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/kick-vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            result.andExpect(status().isConflict())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").doesNotExist(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.message")
                                    .value("이미 투표한 투표한 대상입니다. targetMemberId: 2")
                    );
        }

        @Test
        @DisplayName("과반수 투표로 사용자를 퇴장시킨다.")
        @WithMockCustomUser
        void 성공_과반수_투표로_사용자_퇴장() throws Exception {
            // given
            KickVoteRequest request = new KickVoteRequest(2L, 3);

            // when
            mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/kick-vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            final ResultActions result = mockMvc.perform(
                    post("/{prefix}/agoras/{agoraId}/kick-vote", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(user(new CustomUserDetails(3L, "password", "ROLE_USER")))
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").value("과반수 투표로 사용자를 추방합니다."),
                            jsonPath("$.error").doesNotExist()
                    );
        }
    }
}
