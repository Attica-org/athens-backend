package com.attica.athens.domain.member.dto.response;

import java.time.LocalDateTime;

public record DeleteMemberResponse(
        Long memberId,
        boolean isDeleted,
        LocalDateTime deletedAt,
        String deletedBy
) {
}
