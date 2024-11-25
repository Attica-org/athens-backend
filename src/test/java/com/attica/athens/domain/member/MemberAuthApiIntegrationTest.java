package com.attica.athens.domain.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.support.IntegrationTestSupport;
import com.attica.athens.support.annotation.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("멤버 인증 API 통합 테스트")
public class MemberAuthApiIntegrationTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("OAuth 회원 정보 조회 테스트")
    class GetMemberInfoTest {
        @Test
        @DisplayName("카카오 계정의 회원의 회원정보를 조회한다.")
        @WithMockCustomUser("EnergyAnalystPros")
        void 성공_회원정보조회_카카오회원() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/auth/member/info", API_V1)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.authProvider").value("KAKAO"),
                            jsonPath("$.response.email").exists());
        }

        @Test
        @DisplayName("구글 계정의 회원의 회원정보를 조회한다.")
        @WithMockCustomUser("EnergyAnalystCons")
        void 성공_회원정보조회_구글회원() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    get("/{prefix}/auth/member/info", API_V1)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.authProvider").value("GOOGLE"),
                            jsonPath("$.response.email").exists());
        }
    }
}
