package com.attica.athens.domain.agora.domain;

import static com.attica.athens.domain.agora.domain.AgoraStatus.CLOSED;
import static com.attica.athens.domain.agora.domain.AgoraStatus.QUEUED;
import static com.attica.athens.domain.agora.domain.AgoraStatus.RUNNING;
import static com.attica.athens.support.TestDoubleFactory.createBasicAgoraMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AgoraTest {

    private Category category;

    @BeforeEach
    void setup() {
        category = new Category(0, "category");
    }

    @Test
    @DisplayName("아고라에 사용자를 추가한다.")
    void addMemberAgora() {
        // given
        Agora agora = new Agora("title", 2, 10, "red", 0, 0, category);
        AgoraMember member = createBasicAgoraMember();

        // when
        agora.addMember(member);

        // then
        assertThat(agora.getAgoraMembers().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("아고라 시작 시 상태는 RUNNING 이다.")
    void startAgora() {
        // given
        Agora agora = new Agora("title", 2, 10, "red", 0, 0, category);

        // when
        agora.startAgora();

        // then
        assertThat(agora.getStatus()).isNotEqualTo(QUEUED);
        assertThat(agora.getStatus()).isEqualTo(RUNNING);
    }

    @Test
    @DisplayName("아고라 종료 시 상태는 CLOSED 이다.")
    void endAgora() {
        // given
        Agora agora = new Agora("title", 2, 10, "red", 0, 0, category);

        // when
        agora.endAgora();

        // then
        assertThat(agora.getStatus()).isNotEqualTo(RUNNING);
        assertThat(agora.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    @DisplayName("아고라 시작 상태가 QUEUED가 아니면 예외를 발생시킨다.")
    void startAgoraException() {
        // given
        Agora agora = new Agora("title", 2, 10, "red", 0, 0, category);
        AgoraStatus expectedStatus = AgoraStatus.QUEUED;

        // when
        agora.endAgora();

        // then
        assertThatThrownBy(agora::startAgora)
                .isInstanceOf(InvalidAgoraStatusException.class)
                .hasMessage("Agora status must be " + expectedStatus);
    }

    @Test
    @DisplayName("아고라 상태가 CLOSED 인지 확인한다.")
    void checkClosedStatus() {
        // given
        Agora agora = new Agora("title", 2, 10, "red", 0, 0, category);

        // when
        agora.endAgora();

        // then
        assertThat(agora.getStatus()).isEqualTo(CLOSED);
        assertThat(agora.isClosed()).isTrue();
    }

    @Test
    @DisplayName("아고라 찬반 투표를 반영한다.")
    void updateVote() {
        // given
        Agora agora = new Agora("title", 2, 10, "red", 0, 0, category);

        // when
        agora.updateProsCountAndConsCount(1, 1);

        // then
        assertThat(agora.getProsCount()).isGreaterThan(0);
        assertThat(agora.getProsCount()).isNotZero();
        assertThat(agora.getConsCount()).isGreaterThan(0);
        assertThat(agora.getProsCount()).isNotZero();
    }
}
