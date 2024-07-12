package com.attica.athens.domain.agoraMember.dao;

import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;

public interface AgoraMemberQueryRepository {

    boolean existsNickname(Long agoraId, String nickname);

    int countCapacityByAgoraMemberType(Long agoraId, AgoraMemberType type);
}
