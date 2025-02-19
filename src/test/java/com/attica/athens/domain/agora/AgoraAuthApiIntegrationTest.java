package com.attica.athens.domain.agora;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attica.athens.domain.agora.application.S3ThumbnailService;
import com.attica.athens.domain.agora.dao.CategoryRepository;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.attica.athens.support.IntegrationTestSupport;
import com.attica.athens.support.annotation.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
                            jsonPath("$.error.message").value("Observer cannot send this request")
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
                            jsonPath("$.error.message").value("Observer cannot send this request")
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

    @Nested
    @Sql("/sql/get-category.sql")
    @DisplayName("아고라 생성 테스트")
    @WithMockCustomUser
    class createAgoraTest {

        @MockBean
        private CategoryRepository categoryRepository;

        @MockBean
        private S3ThumbnailService s3ThumbnailService;

        @BeforeEach
        void setup() {
            objectMapper = new ObjectMapper();
            MockitoAnnotations.openMocks(this);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(0, "전체")));
        }

        @Test
        @DisplayName("title이 빈 문자열인 경우 예외를 발생시킨다.")
        void 실패_아고라생성_빈문자열제목() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("", 5, 60, "red", 1L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.title").value("must not be blank")
                    );
        }

        @Test
        @DisplayName("capacity가 1 미만인 경우 예외를 발생시킨다.")
        void 실패_아고라생성_참가자1미만() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-title", 0, 60, "red", 1L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.capacity").value("must be greater than or equal to 1")
                    );
        }

        @Test
        @DisplayName("duration이 1 미만인 경우 예외를 발생시킨다.")
        void 실패_아고라생성_진행시간_1분_미만() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-title", 5, 0, "red", 1L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.duration").value("must be greater than or equal to 1")
                    );
        }

        @Test
        @DisplayName("duration이 180을 초과하는 경우 예외를 발생시킨다.")
        void 실패_아고라생성_진행시간180분초과() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-title", 5, 181, "red", 1L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.duration").value("must be less than or equal to 180")
                    );
        }

        @Test
        @DisplayName("color가 빈 문자열인 경우 예외를 발생시킨다.")
        void 실패_아고라생성_빈문자열색상() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-title", 5, 180, "", 1L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.color").value("must not be blank")
                    );
        }

        @Test
        @DisplayName("categoryId가 null인 경우 예외를 발생시킨다.")
        void 실패_아고라생성_널카테고리() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-title", 5, 180, "red", null);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.categoryId").value("must not be null")
                    );
        }

        @Test
        @DisplayName("categoryId가 존재하지 않은경우 예외를 발생시킨다.")
        void 실패_아고라생성_존재하지않은카테고리() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-title", 5, 180, "red", 3L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found category. categoryId: " + request.categoryId())
                    );
        }

        @Test
        @DisplayName("유효한 파라미터로 아고라를 생성한다.")
        void 성공_아고라생성_유효한파라미터() throws Exception {
            // given
            AgoraCreateRequest request = new AgoraCreateRequest("test-agora", 5, 60, "red", 1L);
            MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes());
            MockMultipartFile jsonRequest = new MockMultipartFile("request", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request));

            // when & then
            mockMvc.perform(multipart("/{prefix}/agoras", API_V1_AUTH)
                            .file(file)
                            .file(jsonRequest)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response.id").value(6),
                            jsonPath("$.error").value(nullValue())
                    );
        }
    }

    @Nested
    @DisplayName("아고라 참가 테스트")
    @Sql("/sql/get-agora.sql")
    @WithMockCustomUser
    class participateAgoraTest {

        @BeforeEach
        void setup() {
            objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("찬성 역할로 아고라에 참가한다.")
        void 성공_아고라참가_찬성역할() throws Exception {
            // given
            Long userId = 1L;
            Long agoraId = 6L;
            AgoraParticipateRequest request = new AgoraParticipateRequest(
                    "test-nickname",
                    1,
                    AgoraMemberType.PROS);

            // when & then
            mockMvc.perform(post("/{prefix}/agoras/{agoraId}/participants", API_V1_AUTH, agoraId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response.agoraId").value(agoraId),
                            jsonPath("$.response.userId").value(userId),
                            jsonPath("$.response.type").value(request.type().toString()),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("반대 역할로 아고라에 참가한다.")
        void 성공_아고라참가_반대역할() throws Exception {
            // given
            Long userId = 1L;
            Long agoraId = 7L;
            AgoraParticipateRequest request = new AgoraParticipateRequest(
                    "test-nickname",
                    1,
                    AgoraMemberType.CONS
            );

            // when & then
            mockMvc.perform(post("/{prefix}/agoras/{agoraId}/participants", API_V1_AUTH, agoraId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response.agoraId").value(agoraId),
                            jsonPath("$.response.userId").value(userId),
                            jsonPath("$.response.type").value(request.type().toString()),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("관찰자 역할로 아고라에 참가한다.")
        void 성공_아고라참가_관찰자역할() throws Exception {
            // given
            Long userId = 1L;
            Long agoraId = 8L;
            AgoraParticipateRequest request = new AgoraParticipateRequest(
                    null,
                    null,
                    AgoraMemberType.OBSERVER);

            // when & then
            mockMvc.perform(post("/{prefix}/agoras/{agoraId}/participants", API_V1_AUTH, agoraId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response.agoraId").value(agoraId),
                            jsonPath("$.response.userId").value(userId),
                            jsonPath("$.response.type").value(request.type().toString()),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("찬성, 반대 역할은 닉네임을 설정하지 않으면 예외를 발생시킨다.")
        void 실패_아고라참가_관찰자역할이아닌경우_비어있는닉네임() throws Exception {
            // given
            Long agoraId = 1L;
            AgoraParticipateRequest request = new AgoraParticipateRequest(
                    "",
                    1,
                    AgoraMemberType.PROS);

            // when & then
            mockMvc.perform(post("/{prefix}/agoras/{agoraId}/participants", API_V1_AUTH, agoraId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1001),
                            jsonPath("$.error.message.nickname").value("nickname can not be null")
                    );
        }
    }

    @Nested
    @DisplayName("아고라 퇴장 테스트")
    @WithMockCustomUser
    class exitAgoraTest {

        @Test
        @DisplayName("아고라에서 퇴장한다.")
        void 성공_아고라퇴장_유효한파라미터() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/exit", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            result.andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.success").value(true),
                            jsonPath("$.response").exists(),
                            jsonPath("$.response.userId").value(1),
                            jsonPath("$.response.type").value("PROS"),
                            jsonPath("$.response.socketDisconnectTime").exists(),
                            jsonPath("$.error").value(nullValue())
                    );
        }

        @Test
        @DisplayName("유저의 ID가 존재하지 않을 경우 예외를 발생시킨다.")
        @WithMockCustomUser("TeacherUnion")
        void 실패_아고라멤버조회_아고라에존재하지않는멤버ID전달() throws Exception {
            // when
            final ResultActions result = mockMvc.perform(
                    patch("/{prefix}/agoras/{agoraId}/exit", API_V1_AUTH, 1)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andExpectAll(
                            jsonPath("$.success").value(false),
                            jsonPath("$.response").value(nullValue()),
                            jsonPath("$.error.code").value(1301),
                            jsonPath("$.error.message").value("Not found user. userId: 4")
                    );
        }
    }

}
