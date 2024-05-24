package com.attica.athens.domain.chat.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.chat.domain.ChatType;
import java.time.LocalDateTime;
import java.util.List;

public record SendMetaResponse(ChatType type, MetaData data) {

    public SendMetaResponse(MetaData data) {
        this(ChatType.META, data);
    }

    public record MetaData(
            AgoraInfo agora,
            List<ParticipantsInfo> participants
    ) {
        public MetaData(Agora agora, List<ParticipantsInfo> participants) {
            this(new AgoraInfo(agora), participants);
        }
    }

    public record AgoraInfo(
            Long id,
            String title,
            LocalDateTime createdAt,
            Integer duration
    ) {
        public AgoraInfo(Agora agora) {
            this(
                    agora.getId(),
                    agora.getTitle(),
                    agora.getCreatedAt(),
                    agora.getDuration()
            );
        }
    }

    public record ParticipantsInfo(
            AgoraUserType type,
            Long count
    ) {
    }
}
