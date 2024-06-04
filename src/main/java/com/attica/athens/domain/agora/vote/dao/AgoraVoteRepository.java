package com.attica.athens.domain.agora.vote.dao;

import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;

public interface AgoraVoteRepository {

    void updateVoteType(Long userId, AgoraVoteRequest agoraVoteRequest, Long agoraId);

    void updateVoteResult(Long agoraId, Integer prosVoteResult, Integer consVoteResult);

    Integer getProsVoteResult(Long agoraId);

    Integer getConsVoteResult(Long agoraId);
}
