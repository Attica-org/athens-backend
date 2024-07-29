package com.attica.athens.domain.agora;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.agora.dto.request.AgoraRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;


public class AgoraOpenApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @Sql(scripts = {
            "/sql/get-category.sql",
            "/sql/get-agora.sql"
    })
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
                            jsonPath("$.error.message").value("{\"status\":\"허용되지 않는 Status 입니다.\"}")
                    );

            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", requestKeyword.status())
                            .param("agora-name", "keyword")
                            .param("next", requestKeyword.next() != null ? requestKeyword.next().toString() : ""))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message").value("{\"status\":\"허용되지 않는 Status 입니다.\"}")
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
                            jsonPath("$.error.message").value("{\"category\":\"must not be null\"}")
                    );
        }

        @Test
        @DisplayName("카테고리로 정렬된 아고라를 조회한다.")
        void 성공_아고라조회_유효한_카테고리() throws Exception {
            // given
            AgoraRequest request = new AgoraRequest("active", 1L, null);

            // when & then
            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", request.status())
                            .param("category", request.category().toString())
                            .param("next", request.next() != null ? request.next().toString() : ""))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response.agoras.length()").value(2),
                            jsonPath("$.response.agoras[0].id").value(2),
                            jsonPath("$.response.agoras[0].agoraTitle").value("Tools"),
                            jsonPath("$.response.agoras[0].agoraColor").value("Red"),
                            jsonPath("$.response.agoras[0].participants.pros").value(0),
                            jsonPath("$.response.agoras[0].participants.cons").value(0),
                            jsonPath("$.response.agoras[0].participants.observer").value(0),
                            jsonPath("$.response.agoras[0].createdAt").value("2023-09-13T14:12:15"),
                            jsonPath("$.response.agoras[0].status").value("QUEUED"),
                            jsonPath("$.response.agoras[1].id").value(1),
                            jsonPath("$.response.agoras[1].agoraTitle").value("Games"),
                            jsonPath("$.response.agoras[1].agoraColor").value("Green"),
                            jsonPath("$.response.agoras[1].participants.pros").value(0),
                            jsonPath("$.response.agoras[1].participants.cons").value(0),
                            jsonPath("$.response.agoras[1].participants.observer").value(0),
                            jsonPath("$.response.agoras[1].createdAt").value("2024-04-01T23:14:09"),
                            jsonPath("$.response.agoras[1].status").value("QUEUED"),
                            jsonPath("$.response.next").value(nullValue()),
                            jsonPath("$.response.hasNext").value(false),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("키워드로 정렬된 아고라를 조회한다.")
        void 성공_아고라조회_유효한_키워드() throws Exception {
            // given
            String keyword = "s";
            SearchKeywordRequest request = new SearchKeywordRequest("active", null);

            // when & then
            mockMvc.perform(get("/{prefix}/agoras", API_V1_OPEN)
                            .param("status", request.status())
                            .param("agora-name", keyword)
                            .param("next", request.next() != null ? request.next().toString() : ""))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response.agoras.length()").value(2),
                            jsonPath("$.response.agoras[0].id").value(2),
                            jsonPath("$.response.agoras[0].agoraTitle").value("Tools"),
                            jsonPath("$.response.agoras[0].agoraColor").value("Red"),
                            jsonPath("$.response.agoras[0].participants.pros").value(0),
                            jsonPath("$.response.agoras[0].participants.cons").value(0),
                            jsonPath("$.response.agoras[0].participants.observer").value(0),
                            jsonPath("$.response.agoras[0].createdAt").value("2023-09-13T14:12:15"),
                            jsonPath("$.response.agoras[0].status").value("QUEUED"),
                            jsonPath("$.response.agoras[1].id").value(1),
                            jsonPath("$.response.agoras[1].agoraTitle").value("Games"),
                            jsonPath("$.response.agoras[1].agoraColor").value("Green"),
                            jsonPath("$.response.agoras[1].participants.pros").value(0),
                            jsonPath("$.response.agoras[1].participants.cons").value(0),
                            jsonPath("$.response.agoras[1].participants.observer").value(0),
                            jsonPath("$.response.agoras[1].createdAt").value("2024-04-01T23:14:09"),
                            jsonPath("$.response.agoras[1].status").value("QUEUED"),
                            jsonPath("$.response.next").value(nullValue()),
                            jsonPath("$.response.hasNext").value(false),
                            jsonPath("$.error").value(nullValue())
                    );
        }
    }
}
