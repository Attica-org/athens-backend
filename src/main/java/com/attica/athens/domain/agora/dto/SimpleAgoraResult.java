package com.attica.athens.domain.agora.dto;

import com.attica.athens.domain.agora.domain.AgoraStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SimpleAgoraResult {
    private Long id;
    private String agora_title;
    private String agora_color;
    private SimpleParticipants participants;
    private LocalDateTime created_at;
    private String status;

    public SimpleAgoraResult(Long id, String agoraTitle, String agoraColor, SimpleParticipants participants,
                             LocalDateTime createdAt, AgoraStatus status) {
        this.id = id;
        this.agora_title = agoraTitle;
        this.agora_color = agoraColor;
        this.participants = participants;
        this.created_at = createdAt;
        this.status = status.getStatus();
    }
}
