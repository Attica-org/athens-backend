package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.support.ValidAgoraParticipateRequest;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;

@ValidAgoraParticipateRequest
public record AgoraParticipateRequest(
    String nickname,
    Integer photoNum,
    String type
) {

    public AgoraUserType getAgoraUserType() {
        return AgoraUserType.valueOf(type);
    }
}
