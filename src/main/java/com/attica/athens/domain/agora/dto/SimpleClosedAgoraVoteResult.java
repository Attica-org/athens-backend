package com.attica.athens.domain.agora.dto;

import com.attica.athens.domain.agora.dao.AgoraResult;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import java.time.LocalDateTime;

public record SimpleClosedAgoraVoteResult(
        Long id,
        Integer prosCount,
        Integer consCount,
        String agoraTitle,
        String agoraColor,
        LocalDateTime createdAt,
        AgoraStatus status
) implements AgoraResult {

    @Override
    public Long id() {
        return id;
    }
}
