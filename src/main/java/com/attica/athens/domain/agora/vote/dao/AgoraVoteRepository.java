package com.attica.athens.domain.agora.vote.dao;

import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;

public interface AgoraVoteRepository {

    AgoraUser updateVoteType(AgoraUser user, AgoraVoteRequest agoraVoteRequest);
}
