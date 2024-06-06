package com.attica.athens.domain.agoraUser.dao;

import com.attica.athens.domain.agoraUser.domain.AgoraUserType;

public interface AgoraUserQueryRepository {

    int countCapacityByAgoraUserType(Long agoraId, AgoraUserType type);
}
