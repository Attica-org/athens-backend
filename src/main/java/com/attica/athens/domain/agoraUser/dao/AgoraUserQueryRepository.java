package com.attica.athens.domain.agoraUser.dao;

import com.attica.athens.domain.agoraUser.domain.AgoraUserType;

public interface AgoraUserQueryRepository {

    boolean existsNickname(Long agoraId, String nickname);

    int countCapacityByAgoraUserType(Long agoraId, AgoraUserType type);
}
