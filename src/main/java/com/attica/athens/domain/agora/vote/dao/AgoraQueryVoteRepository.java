package com.attica.athens.domain.agora.vote.dao;

import com.attica.athens.domain.agora.vote.dto.response.VoteResultResponse;

public interface AgoraQueryVoteRepository {

    VoteResultResponse getVoteResults(Long agoraId);
}
