package com.attica.athens.domain.agora.dao;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.AgoraSlice;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import java.util.List;

public interface AgoraQueryRepository {

    AgoraSlice<SimpleAgoraResult> findAgoraByCategory(Long agoraId, AgoraStatus status, List<String> categories, int size);
}
