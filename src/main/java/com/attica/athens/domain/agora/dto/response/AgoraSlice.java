package com.attica.athens.domain.agora.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class AgoraSlice<T> {
    private final List<T> agoras;
    private final Long next;
    private final boolean hasNext;

    public AgoraSlice(List<T> agoras, Long lastAgoraId, boolean hasNext) {
        this.agoras = agoras;
        this.next = lastAgoraId;
        this.hasNext = hasNext;
    }
}
