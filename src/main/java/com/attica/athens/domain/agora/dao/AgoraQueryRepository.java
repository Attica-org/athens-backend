package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import java.util.List;

public interface AgoraQueryRepository {

    AgoraSlice<SimpleAgoraResult> findAgoraByKeyword(Long agoraId, List<AgoraStatus> status, String keyword);

    AgoraSlice<SimpleAgoraResult> findAgoraByCategory(Long agoraId, List<AgoraStatus> status, List<Long> categoryIds);

    AgoraSlice<SimpleAgoraResult> findAgoraByAllCategory(Long agoraId, List<AgoraStatus> status);

    List<Long> getAgoraIdList();
}
