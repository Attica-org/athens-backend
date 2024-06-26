package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.SimpleClosedAgoraVoteResult;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import java.util.List;
import java.util.Optional;

public interface AgoraQueryRepository {

    AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(Long agoraId, List<AgoraStatus> status, String keyword);

    AgoraSlice<SimpleAgoraResult> findAgoraByCategory(Long agoraId, List<AgoraStatus> status, List<Long> categoryIds);

    AgoraSlice<SimpleAgoraResult> findAgoraByAllCategory(Long agoraId, List<AgoraStatus> status);

    AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraVoteResultsByStatus(Long agoraId, List<AgoraStatus> status);

    List<Long> getAgoraIdList();

    Optional<Agora> findAgoraById(Long agoraId);
}
