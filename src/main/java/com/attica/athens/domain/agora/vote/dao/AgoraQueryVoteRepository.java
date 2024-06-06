package com.attica.athens.domain.agora.vote.dao;

public interface AgoraQueryVoteRepository {

    Integer getProsVoteResult(Long agoraId);

    Integer getConsVoteResult(Long agoraId);
}
