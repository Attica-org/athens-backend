package com.attica.athens.domain.agora.vote.dto.response;

public enum KickVoteResult {
    VOTE_SUCCESS("투표가 성공적으로 처리되었습니다."),
    KICK_REQUIRED("과반수 투표로 사용자를 추방합니다.");

    private final String message;

    KickVoteResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
