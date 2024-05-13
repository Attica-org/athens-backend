package com.attica.athens.domain.agora.dto;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SimpleAgoraResult {
    private Long id;
    private String agoraTitle;
    private String agoraColor;
    private SimpleParticipants participants;
    private LocalDateTime createdAt;
    private String status;

    public SimpleAgoraResult(Long id, String agoraTitle, String agoraColor, SimpleParticipants participants,
                             LocalDateTime createdAt, AgoraStatus status) {
        this.id = id;
        this.agoraTitle = agoraTitle;
        this.agoraColor = agoraColor;
        this.participants = participants;
        this.createdAt = createdAt;
        this.status = status.getStatus();
    }
}
