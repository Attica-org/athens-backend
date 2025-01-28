package com.attica.athens.domain.agora;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.agora.dto.request.AgoraRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;


public class AgoraOpenApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("아고라 Id 리스트 조회")
    class getAgoraIdList {

        @Test
        @DisplayName("아고라 Id 리스트를 조회한다.")
        void 성공_아고라ID리스트조회_존재함() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/agoras/ids", API_V1_OPEN)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.id").isArray(),
                            jsonPath("$.response.id", hasSize(5)),
                            jsonPath("$.response.id[0]").value(1),
                            jsonPath("$.response.id[1]").value(2),
                            jsonPath("$.response.id[2]").value(3),
                            jsonPath("$.response.id[3]").value(4),
                            jsonPath("$.response.id[4]").value(5),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("아고라 Id가 존재하지 않으면 예외를 발생시킨다.")
        @Sql("/sql/agora-id-delete.sql")
        void 실패_아고라ID리스트조회_존재하지않음() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/agoras/ids", API_V1_OPEN)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found agoraId.")
                    );
        }
    }

    @Nested
    @DisplayName("아고라를 조회한다.")
    class getAgoraTest {

        @Test
        @DisplayName("유효하지 않은 상태는 예외를 발생시킨다.")
        void 실패_아고라조회_유효하지않은_상태() throws Exception {
            // given
            AgoraRequest requestCategory = new AgoraRequest("not-allowed-status", 1L, null);
            SearchKeywordRequest requestKeyword = new SearchKeywordRequest("not-allowed-status", null);

            // when & then
            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", requestCategory.status())
                            .param("category", requestCategory.category().toString())
                            .param("next", requestCategory.next() != null ? requestCategory.next().toString() : ""))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.status").value("허용되지 않는 Status 입니다.")
                    );
        }

        @Test
        @DisplayName("null 카테고리는 예외를 발생시킨다.")
        void 실패_아고라조회_null_카테고리() throws Exception {
            // given
            AgoraRequest request = new AgoraRequest("active", null, null);

            // when & then
            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", request.status())
                            .param("category", request.category() != null ? request.category().toString() : "")
                            .param("next", request.next() != null ? request.next().toString() : ""))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.category").value("must not be null")
                    );
        }

        @Test
        @DisplayName("카테고리로 정렬된 아고라를 조회한다.")
        void 성공_아고라조회_유효한_카테고리() throws Exception {
            // given
            AgoraRequest request = new AgoraRequest("active", 6L, null);

            // when & then
            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", request.status())
                            .param("category", request.category().toString())
                            .param("next", request.next() != null ? request.next().toString() : ""))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.error").value(nullValue()),
                            jsonPath("$.response.agoras").isArray(),
                            jsonPath("$.response.agoras.length()").value(1),
                            jsonPath("$.response.agoras[0].id").value(2),
                            jsonPath("$.response.agoras[0].agoraTitle").value("교육 개혁 방안 토론"),
                            jsonPath("$.response.agoras[0].agoraColor").value("#00FF00"),
                            jsonPath("$.response.agoras[0].participants.pros").value(0),
                            jsonPath("$.response.agoras[0].participants.cons").value(0),
                            jsonPath("$.response.agoras[0].participants.observer").value(0),
                            jsonPath("$.response.agoras[0].imageUrl").value(nullValue()),
                            jsonPath("$.response.agoras[0].createdAt").isNotEmpty(),
                            jsonPath("$.response.agoras[0].status").value("RUNNING"),
                            jsonPath("$.response.next").value(nullValue()),
                            jsonPath("$.response.hasNext").value(false)
                    );
        }

        @Test
        @DisplayName("키워드로 정렬된 아고라를 조회한다.")
        void 성공_아고라조회_유효한_키워드() throws Exception {
            // given
            String keyword = "기";
            SearchKeywordRequest request = new SearchKeywordRequest("active", null);

            // when & then
            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", request.status())
                            .param("agora-name", keyword)
                            .param("next", request.next() != null ? request.next().toString() : ""))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.error").value(nullValue()),
                            jsonPath("$.response.agoras").isArray(),
                            jsonPath("$.response.agoras.length()").value(1),
                            jsonPath("$.response.agoras[0].id").value(1),
                            jsonPath("$.response.agoras[0].agoraTitle").value("기후 변화 대책에 대한 토론"),
                            jsonPath("$.response.agoras[0].agoraColor").value("#FF0000"),
                            jsonPath("$.response.agoras[0].participants.pros").value(0),
                            jsonPath("$.response.agoras[0].participants.cons").value(0),
                            jsonPath("$.response.agoras[0].participants.observer").value(0),
                            jsonPath("$.response.agoras[0].imageUrl").value(nullValue()),
                            jsonPath("$.response.agoras[0].createdAt").isNotEmpty(),
                            jsonPath("$.response.agoras[0].status").value("QUEUED"),
                            jsonPath("$.response.next").value(nullValue()),
                            jsonPath("$.response.hasNext").value(false)
                    );
        }
    }

    @Nested
    @DisplayName("아고라 타이틀 조회")
    class getAgoraTitle {
        @Test
        @DisplayName("아고라 타이틀을 조회한다.")
        void 성공_아고라타이틀조회_아고라ID전달() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/agoras/{agoraId}/title", API_V1_OPEN, 1)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.title").value("기후 변화 대책에 대한 토론"),
                            jsonPath("$.response.status").value("QUEUED"),
                            jsonPath("$.error").value(nullValue())
                    );
        }
    }

    @Nested
    @DisplayName("타임아웃 시 아고라를 종료")
    class timeoutAgora {

        @Test
        @DisplayName("타임아웃 시 아고라를 종료한다.")
        void 성공_타임아웃체크_지속시간0인_아고라전달() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/time-out", API_V1_OPEN, 2)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.agoraId").value(2),
                            jsonPath("$.response.isClosed").value(true),
                            jsonPath("$.response.endTime").exists(),
                            jsonPath("$.response.endTime").isString(),
                            jsonPath("$.response.endTime").value(
                                    matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("존재하지 않는 아고라ID 조회시 에러를 발생시킨다.")
        void 실패_아고라조회_존재하지않는아고라ID전달() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/time-out", API_V1_OPEN, 50)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found agora. agoraId: 50")
                    );
        }
    }
}

