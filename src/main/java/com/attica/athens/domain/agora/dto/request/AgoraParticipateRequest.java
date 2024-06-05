package com.attica.athens.domain.agora.dto.request;

import com.attica.athens.domain.agora.support.ValidAgoraParticipateRequest;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.agoraUser.domain.AgoraVoteType;

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
