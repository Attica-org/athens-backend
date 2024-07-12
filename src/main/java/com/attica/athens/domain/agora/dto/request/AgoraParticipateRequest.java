package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.support.ValidAgoraParticipateRequest;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;

@ValidAgoraParticipateRequest
public record AgoraParticipateRequest(
        String nickname,
        Integer photoNum,
        AgoraMemberType type
) {

}
