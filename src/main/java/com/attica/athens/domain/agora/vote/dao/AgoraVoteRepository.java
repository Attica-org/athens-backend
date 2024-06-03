package com.attica.athens.domain.agora.vote.dao;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;

public interface AgoraVoteRepository {

    void updateVoteType(Long userId, AgoraVoteRequest agoraVoteRequest,Long agoraId);

    void updateVoteResult(Agora agoraResult, Long prosVoteResult, Long consVoteResult);

    Long getProsVoteResult(Long agoraId);

    Long getConsVoteResult(Long agoraId);
}
