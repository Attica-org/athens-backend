package com.attica.athens.domain.vote.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.attica.athens.domain.agora.vote.domain.KickVote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KickVoteTest {

    private KickVote kickVote;

    @BeforeEach
    void setUp() {
        kickVote = new KickVote();
    }

    @Test
    @DisplayName("투표 한다.")
    void 성공_투표_참여() {
        assertTrue(kickVote.addVoteMember(1L));
        assertEquals(1, kickVote.getVoteMembers().size());
    }

    @Test
    @DisplayName("중복 투표 금지")
    void 성공_중복투표_금지() {
        assertTrue(kickVote.addVoteMember(1L));
        assertFalse(kickVote.addVoteMember(1L));
        assertEquals(1, kickVote.getVoteMembers().size());
    }

    @Test
    @DisplayName("추방 사용자의 투표는 제거한다.")
    void 성공_사용자투표_제거() {
        kickVote.addVoteMember(1L);
        kickVote.removeVoteMember(1L);
        assertTrue(kickVote.getVoteMembers().isEmpty());
    }

    @Test
    @DisplayName("여러 고유 멤버 투표 추가")
    void 성공_여러_고유_멤버_투표_추가() {
        kickVote.addVoteMember(1L);
        kickVote.addVoteMember(2L);
        kickVote.addVoteMember(3L);
        assertEquals(3, kickVote.getVoteMembers().size());
    }

    @ParameterizedTest
    @DisplayName("퇴장이 가능한지 확인한다.")
    @CsvSource({
            "3, 2, true",
            "5, 3, true",
            "4, 2, false",
            "7, 3, false"
    })
    void 성공_과반수_참여시_퇴장(int currentMemberCount, int voteCount, boolean expected) {
        for (long i = 1; i <= voteCount; i++) {
            kickVote.addVoteMember(i);
        }

        assertEquals(expected, kickVote.kickPossible(currentMemberCount));
    }
}