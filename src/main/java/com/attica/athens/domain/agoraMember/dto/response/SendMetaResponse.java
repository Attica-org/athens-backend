package com.attica.athens.domain.agoraMember.dto.response;

import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.attica.athens.domain.chat.domain.ChatType;
import java.time.LocalDateTime;
import java.util.List;

public record SendMetaResponse(ChatType type, MetaData data) {

    public SendMetaResponse(MetaData data) {
        this(ChatType.META, data);
    }

    public record MetaData(
            List<ParticipantsInfo> participants,
            AgoraInfo agora,
            AgoraMemberInfo agoraMemberInfo
    ) {
        public MetaData(List<ParticipantsInfo> participants, Agora agora, AgoraMember agoraMember) {
            this(participants, new AgoraInfo(agora), new AgoraMemberInfo(agoraMember));
        }
    }

    public record AgoraInfo(
            Long id,
            String title,
            LocalDateTime createdAt,
            Integer duration,
            LocalDateTime startAt
    ) {
        public AgoraInfo(Agora agora) {
            this(
                    agora.getId(),
                    agora.getTitle(),
                    agora.getCreatedAt(),
                    agora.getDuration(),
                    agora.getStartTime()
            );
        }
    }

    public record ParticipantsInfo(
            AgoraMemberType type,
            Long count
    ) {
    }

    public record AgoraMemberInfo(
            Long memberId,
            String username,
            LocalDateTime socketDisconnectTime
    ) {
        public AgoraMemberInfo(AgoraMember agoraMember) {
            this(agoraMember.getId(), agoraMember.getNickname(), agoraMember.getSocketDisconnectTime());
        }
    }
}
