package com.attica.athens.domain.agora.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class AgoraSlice<T> {
    private final List<T> agoras;
    private final Long lastAgoraId;
    private final boolean hasNext;

    public AgoraSlice(List<T> agoras, Long lastAgoraId, boolean hasNext) {
        this.agoras = agoras;
        this.lastAgoraId = lastAgoraId;
        this.hasNext = hasNext;
    }
}
