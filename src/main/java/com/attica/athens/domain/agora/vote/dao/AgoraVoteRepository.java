package com.attica.athens.domain.agora.vote.dao;

import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;

public interface AgoraVoteRepository {

    AgoraUser updateVoteType(Long userId, AgoraVoteType voteType, Boolean isOpinionVoted, Long agoraId);

    void updateVoteResult(Long agoraId, Integer prosVoteResult, Integer consVoteResult);

    Integer getProsVoteResult(Long agoraId);

    Integer getConsVoteResult(Long agoraId);
}
