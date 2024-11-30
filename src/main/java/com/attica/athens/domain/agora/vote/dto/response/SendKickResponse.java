package com.attica.athens.domain.agora.vote.dto.response;

import com.attica.athens.domain.chat.domain.ChatType;

public record SendKickResponse(ChatType type, KickVoteInfo kickVoteInfo) {

    public SendKickResponse(KickVoteInfo kickVoteInfo) {
        this(ChatType.KICK, kickVoteInfo);
    }

    public record KickVoteInfo(Long targetMemberId, String message) {

        public KickVoteInfo(Long targetMemberId) {
            this(targetMemberId, "사용자를 추방합니다. memberId: " + targetMemberId);
        }
    }
}
