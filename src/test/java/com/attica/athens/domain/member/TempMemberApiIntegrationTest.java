package com.attica.athens.domain.member;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.support.IntegrationTestSupport;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("임시 유저 API 통합 테스트")
public class TempMemberApiIntegrationTest extends IntegrationTestSupport {

    @Test
    @DisplayName("임시 유저를 생성한다.")
    void 성공_임시유저생성_유효한파라미터전달() throws Exception {
        // when
        final ResultActions result = mockMvc.perform(
                post("/{prefix}/temp-user", API_V1)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.success").value(true),
                        jsonPath("$.response").exists(),
                        jsonPath("$.response.accessToken").isNotEmpty(),
                        jsonPath("$.response.accessToken").exists(),
                        jsonPath("$.response.accessToken").value(
                                Matchers.matchesRegex("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$")),
                        jsonPath("$.error").value(nullValue())
                );
    }
}
