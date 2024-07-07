package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.SimpleClosedAgoraVoteResult;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import java.util.List;
import java.util.Optional;

public interface AgoraQueryRepository {

    AgoraSlice<SimpleAgoraResult> findActiveAgoraByKeyword(Long agoraId, List<AgoraStatus> status, String keyword);

    AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraByKeyword(Long agoraId, List<AgoraStatus> status,
                                                                     String keyword);

    AgoraSlice<SimpleAgoraResult> findActiveAgoraByCategory(Long agoraId, List<AgoraStatus> status, List<Long> categoryIds);

    AgoraSlice<SimpleAgoraResult> findActiveAgoraByAllCategory(Long agoraId, List<AgoraStatus> status);

    AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraVoteResultsByCategory(Long agoraId,
                                                                                 List<Long> categoryIds,
                                                                                 List<AgoraStatus> status);

    AgoraSlice<SimpleClosedAgoraVoteResult> findClosedAgoraVoteResultsByAllCategory(Long agoraId,
                                                                                    List<AgoraStatus> status);

    List<Long> getAgoraIdList();

    Optional<Agora> findAgoraById(Long agoraId);
}
