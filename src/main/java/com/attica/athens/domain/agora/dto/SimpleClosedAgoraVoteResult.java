package com.attica.athens.domain.agora.dto;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import java.time.LocalDateTime;

public record SimpleClosedAgoraVoteResult(
        Long id,
        Integer prosCount,
        Integer consCount,
        Integer totalMember,
        String agoraTitle,
        String agoraColor,
        String imageUrl,
        LocalDateTime createdAt,
        AgoraStatus status
) {

}
