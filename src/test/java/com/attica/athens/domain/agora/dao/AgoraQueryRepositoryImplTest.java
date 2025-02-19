package com.attica.athens.domain.agora.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.attica.athens.config.TestAuditingConfiguration;
import com.attica.athens.config.TestQueryDslConfig;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.domain.Category;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.SimpleClosedAgoraVoteResult;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Import({
        TestQueryDslConfig.class,
        TestAuditingConfiguration.class
})
@DataJpaTest
@EnableJpaAuditing
class AgoraQueryRepositoryImplTest {

    @Autowired
    private AgoraRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setup() {
        category = new Category(0, "특정 카테고리");
        categoryRepository.save(category);
    }

    @Test
    @DisplayName("ID로 아고라를 조회한다.")
    void 성공_아고라조회_존재하는_ID() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        // when
        Agora result = repository.findById(agora.getId()).orElseThrow();

        // then
        assertThat(result.getId()).isEqualTo(agora.getId());
    }

    @Test
    @DisplayName("30개 이하 아고라를 가져온다.")
    void 성공_아고라조회_30개이하() {
        // given
        List<Agora> agoras = new ArrayList<>();
        String title = "title";
        for (int i = 0; i < 5; i++) {
            agoras.add(new Agora(title + i, 2, 10, "red", 0, 0, category, null));
        }

        repository.saveAll(agoras);

        // when
        List<Long> result = repository.getAgoraIdList();

        // then
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("키워드로 종료되지 않은 아고라를 조회한다.")
    void 성공_아고라조회_유효한키워드_종료되지않은상태() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        String keyword = "title";
        List<AgoraStatus> statuses = List.of(AgoraStatus.QUEUED, AgoraStatus.RUNNING);

        // when
        AgoraSlice<SimpleAgoraResult> result = repository.findActiveAgoraByKeyword(null, statuses, keyword);
        SimpleAgoraResult first = result.getAgoras().get(0);

        // then
        assertThat(result.getAgoras()).hasSize(1);
        assertThat(first.agoraTitle()).isEqualTo("title");
        assertThat(first.id()).isEqualTo(agora.getId());
        assertThat(first.status()).isIn(statuses);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("키워드로 종료된 아고라를 조회한다.")
    void 성공_아고라조회_유효한키워드_종료된상태() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        agora.endAgora();
        List<AgoraStatus> statuses = List.of(AgoraStatus.CLOSED);
        String keyword = "title";

        // when
        AgoraSlice<SimpleClosedAgoraVoteResult> result = repository.findClosedAgoraByKeyword(null, statuses, keyword);
        SimpleClosedAgoraVoteResult first = result.getAgoras().get(0);

        // then
        assertThat(result.getAgoras()).hasSize(1);
        assertThat(first.agoraTitle()).isEqualTo("title");
        assertThat(first.id()).isEqualTo(agora.getId());
        assertThat(first.status()).isIn(statuses);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("특정 카테고리로 종료되지 않은 아고라를 조회한다.")
    void 성공_아고라조회_특정카테고리_종료되지않은상태() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        List<AgoraStatus> statuses = List.of(AgoraStatus.QUEUED, AgoraStatus.RUNNING);
        List<Long> categoryIds = List.of(category.getId());

        // when
        AgoraSlice<SimpleAgoraResult> result = repository.findActiveAgoraByCategory(null, statuses,
                categoryIds);
        SimpleAgoraResult first = result.getAgoras().get(0);

        // then
        assertThat(result.getAgoras()).hasSize(1);
        assertThat(first.agoraTitle()).isEqualTo("title");
        assertThat(first.id()).isEqualTo(agora.getId());
        assertThat(first.status()).isIn(statuses);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("전체 카테고리로 종료되지 않은 아고라를 조회한다.")
    void 성공_아고라조회_전체카테고리_종료되지않은상태() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        List<AgoraStatus> statuses = List.of(AgoraStatus.QUEUED, AgoraStatus.RUNNING);

        // when
        AgoraSlice<SimpleAgoraResult> result = repository.findActiveAgoraByAllCategory(null, statuses);
        SimpleAgoraResult first = result.getAgoras().get(0);

        // then
        assertThat(result.getAgoras()).hasSize(1);
        assertThat(first.agoraTitle()).isEqualTo("title");
        assertThat(first.id()).isEqualTo(agora.getId());
        assertThat(first.status()).isIn(statuses);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("특정 카테고리로 종료된 아고라를 조회한다.")
    void 성공_아고라조회_특정카테고리_종료된상태() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        agora.endAgora();
        List<AgoraStatus> statuses = List.of(AgoraStatus.CLOSED);
        List<Long> categoryIds = List.of(category.getId());

        // when
        AgoraSlice<SimpleClosedAgoraVoteResult> result = repository.findClosedAgoraVoteResultsByCategory(
                null, categoryIds, statuses);
        SimpleClosedAgoraVoteResult first = result.getAgoras().get(0);

        // then
        assertThat(result.getAgoras()).hasSize(1);
        assertThat(first.agoraTitle()).isEqualTo("title");
        assertThat(first.id()).isEqualTo(agora.getId());
        assertThat(first.status()).isIn(statuses);
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("전체 카테고리로 종료된 아고라를 조회한다.")
    void 성공_아고라조회_전체카테고리_종료된상태() {
        // given
        Agora agora = repository.save(
                new Agora("title", 2, 10, "red", 0, 0, category, null));

        agora.endAgora();
        List<AgoraStatus> statuses = List.of(AgoraStatus.CLOSED);

        // when
        AgoraSlice<SimpleClosedAgoraVoteResult> result = repository.findClosedAgoraVoteResultsByAllCategory(
                null, statuses);
        SimpleClosedAgoraVoteResult first = result.getAgoras().get(0);

        // then
        assertThat(result.getAgoras()).hasSize(1);
        assertThat(first.agoraTitle()).isEqualTo("title");
        assertThat(first.id()).isEqualTo(agora.getId());
        assertThat(first.status()).isIn(statuses);
        assertThat(result.isHasNext()).isFalse();
    }
}
