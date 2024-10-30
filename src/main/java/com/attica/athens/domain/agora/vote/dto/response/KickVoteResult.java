package com.attica.athens.domain.agora.vote.dto.response;

public enum KickVoteResult {
    VOTE_SUCCESS("투표가 성공적으로 처리되었습니다."),
    KICK_REQUIRED("강제 퇴장이 필요합니다.");

    private final String message;

    KickVoteResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
