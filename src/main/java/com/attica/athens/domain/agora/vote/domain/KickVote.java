package com.attica.athens.domain.agora.vote.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public class KickVote {

    private Set<Long> voteMembers = new HashSet<>();

    public boolean addVoteMember(Long memberId) {
        return voteMembers.add(memberId);
    }

    public void removeVoteMember(Long memberId) {
        voteMembers.remove(memberId);
    }

    public boolean kickPossible(int currentMemberCount) {
        return voteMembers.size() > currentMemberCount / 2;
    }
}
