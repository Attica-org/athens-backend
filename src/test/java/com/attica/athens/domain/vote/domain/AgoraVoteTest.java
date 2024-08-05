package com.attica.athens.domain.vote.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.exception.InvalidAgoraVoteTypeException;
import com.attica.athens.domain.agora.vote.exception.VoteTimeOutException;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraVoteType;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("투표 테스트")
class AgoraVoteTest {

    @Test
    @DisplayName("투표를 하면 투표타입을 update하고 투표타입을 결과로 보여준다.")
    void 성공_투표업데이트_유효한파라미터사용() {
        // given
        AgoraMember agoraMember = AgoraMember.builder().build();

        // when
        agoraMember.updateIsOpinionVotedAndVoteType(AgoraVoteType.PROS, true);

        // then
        then(agoraMember.getVoteType()).isEqualTo(AgoraVoteType.PROS);
        then(agoraMember.getIsOpinionVoted()).isEqualTo(true);
    }

    @Test
    @DisplayName("찬반 투표 집계 결과를 update하면 update된 투표 결과를 반환한다.")
    void 성공_투표결과업데이트_찬반집계결과사용() {
        // given
        Agora agora = Agora.builder().build();

        // when
        agora.updateProsCountAndConsCount(15, 8);

        // then
        then(agora.getProsCount()).isEqualTo(15);
        then(agora.getConsCount()).isEqualTo(8);
    }

    @Test
    @DisplayName("투표 응답 형식이 알맞지 않다면 예외를 응답한다.")
    void 실패_투표형식_유효하지않는투표타입() {
        // given
        AgoraVoteRequest request = new AgoraVoteRequest(null, true);
        Agora agora = Agora.builder().build();

        // when & then
        thenThrownBy(() -> agora.checkAgoraVoteRequest(request))
                .isInstanceOf(InvalidAgoraVoteTypeException.class)
                .hasMessage("Agora VoteType must be PROS or CONS");
    }

    @Test
    @DisplayName("아고라 상태가 QUEUED 또는 RUNNING 상태면 예외를 응답한다.")
    void 실패_아고라상태_유효하지않는아고라상태() {
        // given
        Agora agora = Agora.builder().build();

        // when & then
        thenThrownBy(() -> agora.checkAgoraStatus())
                .isInstanceOf(InvalidAgoraStatusException.class)
                .hasMessage("Agora status must be CLOSED");
    }

    @Test
    @DisplayName("투표 시간이 지나면 예외가 발생한다.")
    void 실패_투표시간초과_만료된시간전달() throws Exception {
        // given
        Agora agora = Agora.builder().build();
        LocalDateTime expiredTime = LocalDateTime.now().plusSeconds(30);
        setEndTimeUsingReflection(agora, expiredTime);

        // when & then
        thenThrownBy(() -> agora.checkVoteTime())
                .isInstanceOf(VoteTimeOutException.class)
                .hasMessage("Voting period has expired.");
    }

    private void setEndTimeUsingReflection(Agora agora, LocalDateTime endTime) throws Exception {
        Field endTimeField = Agora.class.getDeclaredField("endTime");
        endTimeField.setAccessible(true);
        endTimeField.set(agora, endTime);
    }
}
